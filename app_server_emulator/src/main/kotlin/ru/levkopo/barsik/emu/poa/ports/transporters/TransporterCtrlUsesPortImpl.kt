package ru.levkopo.barsik.emu.poa.ports.transporters

import DSP.GenericSignalParams
import DSP.PowerPhase
import DSP.SignalDataEx
import DSP.SignalMsg
import DSP.SignalRep
import DSP.TransporterCtrlUsesPort_v3POA
import kotlinx.coroutines.flow.MutableStateFlow
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.emu.Modulator
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterDataPortImpl.TransporterController
import ru.levkopo.barsik.emu.str

class TransporterCtrlUsesPortImpl(
    private val application: ApplicationImpl
) : TransporterCtrlUsesPort_v3POA() {
    companion object {
        val inputMessage = MutableStateFlow("")
        val outputMessage = MutableStateFlow("")
        val modulator = Modulator()
    }

    override fun SendTest() {
        TODO("Not yet implemented")
    }

    override fun SendSignalMessage(message: SignalMsg) {
        modulator.carrierFrequency = message.params.freq - message.params.width / 2
        inputMessage.tryEmit(message.str())

        val newMessage = SignalMsg(
            GenericSignalParams(
                when(message.packetNumber){
                    0 -> message.params.freq
                    else -> 0.0
                },
                0,
                0.0,
                0.0,
                0.0,
                message.params.width,
                message.params.filter,
                message.params.qualityPhase,
                message.params.ae,
                message.params.channel,
                message.params.octets,
            ),
            message.packetNumber,
            message.b,
            message.c,
            message.d,
            1,
            false,
            SignalDataEx(
                arrayOf(),
                arrayOf(),
                Array((message.params.width / 1000).toInt()) {
                    modulator.currentIQ
                }.flatten().toTypedArray(),
                PowerPhase(
                    byteArrayOf(),
                    byteArrayOf(),
                    1
                ),
                SignalRep(
                    1
                )
            ),
            _orb().create_any().apply {
                type(_orb().get_primitive_tc(TCKind.tk_void))
            }
        )

        val port = application.getConnectedPort("DataConnection") as TransporterController
        Thread.sleep(10)
        port.sendSignalMessage(newMessage)
        outputMessage.tryEmit(newMessage.str())
    }


    override fun SendPowerPhaseQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendIQSpectrumQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun getSigBoardInfo(): Int {
        TODO("Not yet implemented")
    }

    override fun releaseFifo() {
        println("TransporterCtrlUsesPort_v3POA.releaseFifo")
    }
}