package ru.levkopo.barsik.data.repositories

import CF.PortHelper
import CF.ResourceHelper
import DSP.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.omg.CORBA.ORB
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.IqDataSource
import ru.levkopo.barsik.data.RealTimeAmDemodulator
import ru.levkopo.barsik.data.remote.SignalOrbManager
import ru.levkopo.barsik.models.asString
import ru.levkopo.barsik.ui.SignalSettings
import ru.levkopo.barsik.ui.SignalSettings.Scale
import kotlin.concurrent.thread
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object SignalRepository : IqDataSource {
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
    private val amDemodulator = RealTimeAmDemodulator(this)
    private var _isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    private val _currentSpectrum = MutableStateFlow<List<Signal>>(emptyList())
    val currentSpectrum = _currentSpectrum.asStateFlow()

    private val _savedSignalTable = MutableStateFlow<Map<Double, Signal>>(emptyMap())
    val savedSignalTable = _savedSignalTable.asStateFlow()

    private val currentSignalMsg: MutableStateFlow<SignalMsg?> = MutableStateFlow(null)
    private var packetNumber = 0
    private val serverTransporterFlow = MutableStateFlow<TransporterCtrlUsesPort?>(null)

    private val transporter = object : TransporterCtrlUsesPort_v3POA() {
        override fun SendPowerPhaseQuery(): Int = 0
        override fun SendIQSpectrumQuery(): Int = 0
        override fun getSigBoardInfo(): Int = 0
        override fun releaseFifo() {}

        override fun SendTest() {
            _isInitialized.value = true
        }

        private lateinit var iqData: DoubleArray

        override fun SendSignalMessage(message: SignalMsg) {
            currentSignalMsg.value = message
            sendClientSignalMsg()
        }
    }

    init {
        thread {
            runBlocking {
                currentSignalMsg.filterNotNull().collectLatest { message ->
                    runCatching {
                        val iqSamples = message.extended.c

                        val startFrequency = SignalConfig.frequency - (SignalConfig.width / 2)
                        val endFrequency = SignalConfig.frequency + (SignalConfig.width / 2)

                        val result = arrayListOf<Signal>()
                        var currentFrequency = startFrequency
                        var numOfSignal = 0
                        while (currentFrequency < endFrequency) {
                            val iqSample = iqSamples.getOrNull(numOfSignal++) ?: break
                            val amplitude = sqrt(iqSample.i.pow(2) + iqSample.q.pow(2)).toDouble()
                            val vrms = amplitude / sqrt(2f)
                            val power = vrms.pow(2) / 50

                            result.add(
                                Signal(
                                    frequency = currentFrequency,
                                    voltage = amplitude,
                                    dBm = when (power) {
                                        0.0 -> 0.0
                                        else -> 10 * log10(power / 0.001)
                                    }
                                )
                            )

                            currentFrequency += SignalConfig.filter
                        }

                        _currentSpectrum.value = result
                    }
                }
            }
        }

        scope.launch {
            _currentSpectrum.collect { signals ->
                val saved = HashMap(_savedSignalTable.value)
                for (signal in signals) {
                    val currentAmplitude = signal.getScale(SignalSettings.graphScale.value)
                    val minAmplitude = SignalSettings.detectorAmplitude.value
                    if (currentAmplitude > SignalSettings.detectorAmplitude.value) {
                        val savedFrequency = saved[signal.frequency]?.getScale(SignalSettings.graphScale.value) ?: 0.0
                        if (currentAmplitude > savedFrequency) {
                            saved[signal.frequency] = signal
                        }
                    }
                }

                _savedSignalTable.value = saved
            }
        }

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

    fun startSignalDataExchange() {
        _isRunning.value = true
//        amDemodulator.start()
        sendClientSignalMsg()
    }

    fun stopSignalDataExchange() {
        amDemodulator.stopRunning()
        _isRunning.value = false
    }

    fun clearTable() {
        _savedSignalTable.value = emptyMap()
    }

    override fun getNextIqSamples(count: Int): List<Pair<Double, Double>> {
        val result = ArrayList<Pair<Double, Double>>()
        try {
            val startFrequency = SignalConfig.frequency - (SignalConfig.width / 2)
            val frequency = 101.2 * 1e6
            val position = ((frequency - startFrequency) / SignalConfig.filter).roundToInt()
            while (result.size <= count) {
                val iqSamples = currentSignalMsg.value?.extended?.c ?: continue
                result.add(iqSamples[position].i.toDouble() to iqSamples[position].q.toDouble())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }
}