package ru.levkopo.barsik.data.repositories

import CF.PortHelper
import CF.ResourceHelper
import DSP.GenericSignalParams
import DSP.PowerPhase
import DSP.SignalDataEx
import DSP.SignalMsg
import DSP.SignalRep
import DSP.TransporterCtrlUsesPort
import DSP.TransporterCtrlUsesPortHelper
import DSP.TransporterCtrlUsesPort_v3POA
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.jtransforms.fft.DoubleFFT_1D
import org.omg.CORBA.ORB
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.configs.DetectorsConfig
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.remote.SignalOrbManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

object SignalRepository {
    data class Signal(val frequency: Double, val amplitude: Double)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var _isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    private val _fftResult = MutableStateFlow<List<Signal>>(listOf())
    val fftResult = _fftResult.asStateFlow()

    private val _savedSignalTable = MutableStateFlow<Map<Double, Double>>(emptyMap())
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

            runCatching {
                val sizeOfIq = message.extended.c.size
                if(!::iqData.isInitialized || iqData.size != sizeOfIq) {
                    iqData = DoubleArray(sizeOfIq * 2)
                }

                _fftResult.value = List(sizeOfIq) { i ->
                    val iq = message.extended.c[i]
                    Signal(i.toDouble(), abs((iq.i + iq.q).toDouble()))
                }
            }.onFailure {
                it.printStackTrace()
            }

            sendClientSignalMsg()
        }
    }

    init {
        scope.launch {
            _fftResult.collect { signals ->
                val saved = HashMap(_savedSignalTable.value)
                for (signal in signals) {
                    if(signal.amplitude > DetectorsConfig.minAmplitude) {
                        val savedFrequency = saved[signal.frequency] ?: 0.0
                        if (signal.amplitude > savedFrequency) {
                            saved[signal.frequency] = signal.amplitude
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
        sendClientSignalMsg()
    }

    fun stopSignalDataExchange() {
        _isRunning.value = false
    }
}