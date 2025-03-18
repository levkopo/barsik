package ru.levkopo.barsik.data.repositories

import CF.PortHelper
import CF.ResourceHelper
import DSP.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.omg.CORBA.ORB
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.remote.SignalOrbManager
import ru.levkopo.barsik.ui.SignalSettings
import ru.levkopo.barsik.ui.SignalSettings.Scale
import kotlin.concurrent.thread
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

object SignalRepository {
    data class Signal(
        val frequency: Double,
        val voltage: Double,
        val dBm: Double,
    ) {
        fun getScale(scale: Scale): Double = when (scale) {
            Scale.MICRO_VOLT -> voltage
            Scale.DBM -> dBm
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Информация об состоянии приема новых сигналов
     */
    private var _isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    /**
     * Информация об инициализации сервера
     */
    private val _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    /**
     * Текущий спектр
     */
    private val _currentSpectrum = MutableStateFlow<List<Signal>>(emptyList())
    val currentSpectrum = _currentSpectrum.asStateFlow()

    /**
     * Таблица сигналов, зафиксированная детектором сигналов
     */
    private val _savedSignalTable = MutableStateFlow<Map<Double, Signal>>(emptyMap())
    val savedSignalTable = _savedSignalTable.asStateFlow()

    /**
     * Текущее последнее сигнальное сообщение от сервера
     */
    private val currentSignalMsg: MutableStateFlow<SignalMsg?> = MutableStateFlow(null)

    /**
     * Порт для отправки запросов клиентом к серверу об получении сигнальной информации
     */
    private val serverTransporterFlow = MutableStateFlow<TransporterCtrlUsesPort?>(null)

    /**
     * Номер отправленного пакета
     */
    private var packetNumber = 0

    /**
     * Порт, используемый сервером для отправки сигналов клиенту
     */
    private val transporter = object : TransporterCtrlUsesPort_v3POA() {
        override fun SendPowerPhaseQuery(): Int = 0
        override fun SendIQSpectrumQuery(): Int = 0
        override fun getSigBoardInfo(): Int = 0
        override fun releaseFifo() {}

        override fun SendTest() {
            // После того как сервер отправил тестовый сигнал, система считается полностью инициализированной.
            // Разрешаем пользователю работать с настройками и приемом сигналов
            _isInitialized.value = true
        }

        override fun SendSignalMessage(message: SignalMsg) {
            // Сохранение нового пакета сигналов
            currentSignalMsg.value = message

            // Отправляем новый пакет с запросом
            sendClientSignalMsg()
        }
    }

    init {
        // Запуск потока для обработки сигналов
        thread {
            runBlocking {
                currentSignalMsg.filterNotNull().collectLatest { message ->
                    runCatching {
                        // Получаем iq сигналы
                        val iqSamples = message.extended.c

                        // Расчет первой частоты в спектре
                        val startFrequency = SignalConfig.frequency - (SignalConfig.width / 2)

                        // Расчет последней частоты в спектре
                        val endFrequency = SignalConfig.frequency + (SignalConfig.width / 2)

                        // Создание списка с обработанными сигналами
                        val result = arrayListOf<Signal>()

                        // Определяем частоту, с которой начнется отсчет и обработка
                        var currentFrequency = startFrequency

                        // Номер обработанного сигнала
                        var numOfSignal = 0

                        while (currentFrequency < endFrequency) {
                            // Получение iq сигнала по номеру и увеличиваем на 1 для следующего цикла.
                            // Если сигнала нет в списке, цикл останавливается
                            val iqSample = iqSamples.getOrNull(numOfSignal++) ?: break

                            // Расчет пиковой (реального) значения амплитуды сигнала
                            val amplitude = sqrt(iqSample.i.pow(2) + iqSample.q.pow(2)).toDouble()

                            // Расчет среднеквадратичного значения амплитуды сигнала
                            val vrms = amplitude / sqrt(2f)

                            // Расчет мощности при импедансе 50 Ом
                            val power = vrms.pow(2) / 50

                            // Расчет мощности в дБм
                            val dbm = when (power) {
                                0.0 -> 0.0
                                else -> 10 * log10(power / 0.001)
                            }

                            result.add(
                                Signal(
                                    frequency = currentFrequency,
                                    voltage = amplitude,
                                    dBm = dbm
                                )
                            )

                            // Добавление частоты фильтра к частоте следующего сигнала цикла
                            currentFrequency += SignalConfig.filter
                        }

                        // Сохранение результата
                        _currentSpectrum.value = result
                    }
                }
            }
        }

        // Запуск потока для детектора
        scope.launch {
            _currentSpectrum.collect { signals ->
                val saved = HashMap(_savedSignalTable.value)
                for (signal in signals) {
                    val currentAmplitude = signal.getScale(SignalSettings.graphScale.value)
                    val minAmplitude = SignalSettings.detectorAmplitude.value
                    if (currentAmplitude > minAmplitude) {
                        val savedFrequency = saved[signal.frequency]?.getScale(SignalSettings.graphScale.value) ?: 0.0
                        if (currentAmplitude > savedFrequency) {
                            saved[signal.frequency] = signal
                        }
                    }
                }

                _savedSignalTable.value = saved
            }
        }

        // Запуск потока для подключения порта клиента
        SignalOrbManager.useApplication { application ->
            if (application == null) return@useApplication

            val scheduler = ResourceHelper.narrow(application.getPort("Scheduler"))
            serverTransporterFlow.value = TransporterCtrlUsesPortHelper.narrow(scheduler.getPort("TransporterCtrlPort"))

            val transporterDataPort = PortHelper.narrow(scheduler.getPort("TransporterDataPort"))
            SignalOrbManager.usePOA { poa ->
                poa.activate_object(transporter)
                transporterDataPort.connectPort(
                    PortHelper.unchecked_narrow(
                        poa.servant_to_reference(
                            transporter,
                        )
                    ),
                    "DataConnection"
                )
            }
        }.launchIn(scope)
    }

    /**
     * Функция для сборки сообщения запроса клиента
     */
    private fun buildNewRequestSignalMsg(orb: ORB) = SignalMsg(
        GenericSignalParams(
            SignalConfig.frequency,
            SignalConfig.attenuator,
            SignalConfig.c,
            SignalConfig.d,
            SignalConfig.f,
            SignalConfig.width,
            SignalConfig.filter,
            SignalConfig.qualityPhase,
            SignalConfig.ae,
            SignalConfig.channel,
            byteArrayOf(8)
        ),
        packetNumber++,
        0, true, 1, 0, false,
        SignalDataEx(
            arrayOf(), arrayOf(), arrayOf(),
            PowerPhase(
                byteArrayOf(),
                byteArrayOf(),
                -70
            ),
            SignalRep(1)
        ),
        orb.create_any().apply {
            type(orb.get_primitive_tc(TCKind.tk_null))
        }
    )

    /**
     * Функция для отправки сообщения запроса клиента
     */
    private fun sendClientSignalMsg() {
        if (!_isRunning.value) {
            return
        }

        val transporterPool = serverTransporterFlow.value
        if (transporterPool == null) {
            _isRunning.value = false
            return
        }

        val message = buildNewRequestSignalMsg(transporterPool._get_orb())
        transporterPool.SendSignalMessage(message)
    }

    /**
     * Функция для запуска обмена сигнальными сообщениями
     */
    fun startSignalDataExchange() {
        _isRunning.value = true
        sendClientSignalMsg()
    }

    /**
     * Функция для остановки обмена сигнальными сообщениями
     */
    fun stopSignalDataExchange() {
        _isRunning.value = false
    }

    /**
     * Очистка таблицы детектора
     */
    fun clearTable() {
        _savedSignalTable.value = emptyMap()
    }
}