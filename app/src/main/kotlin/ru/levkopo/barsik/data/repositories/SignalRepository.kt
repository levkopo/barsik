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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import org.omg.CORBA.ORB
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.remote.SignalOrbManager

object SignalRepository {
    private var isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val currentSignalMsg: MutableStateFlow<SignalMsg?> = MutableStateFlow(null)
    private var packetNumber = -1
    private val serverTransporterFlow = MutableStateFlow<TransporterCtrlUsesPort?>(null)
    private val transporter = object : TransporterCtrlUsesPort_v3POA() {
        override fun SendPowerPhaseQuery(): Int = 0
        override fun SendIQSpectrumQuery(): Int = 0
        override fun getSigBoardInfo(): Int = 0
        override fun releaseFifo() {}

        override fun SendTest() {
            isInitialized.value = true
        }

        override fun SendSignalMessage(message: SignalMsg) {
            currentSignalMsg.value = message
            sendClientSignalMsg()
        }
    }

    init {

        SignalOrbManager.useApplication { application ->
            if(application == null) return@useApplication

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
        }
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
            byteArrayOf(10)
        ),
        packetNumber++,
        0, true, 1, 0, false,
        SignalDataEx(
            arrayOf(), arrayOf(), arrayOf(),
            PowerPhase(
                byteArrayOf(),
                byteArrayOf(),
                1
            ),
            SignalRep(
                -70
            )
        ),
        orb.create_any().apply {
            type(orb.get_primitive_tc(TCKind.tk_null))
        }
    )

    private fun sendClientSignalMsg() {
        if(!isRunning.value) {
            return
        }

        val transporterPool = serverTransporterFlow.value
        if(transporterPool == null) {
            isRunning.value = false
            return
        }

        val message = buildNewRequestSignalMsg(transporterPool._get_orb())
        transporterPool.SendSignalMessage(message)
    }

    fun isRunning() = isRunning.asStateFlow()

    fun startSignalDataExchange() {
        isRunning.value = true
        sendClientSignalMsg()
    }

    fun stopSignalDataExchange() {
        isRunning.value = false
    }
}