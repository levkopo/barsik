package ru.levkopo.barsik.emu.poa.ports.transporters

import DSP.GenericSignalParams
import DSP.PowerPhase
import DSP.SignalDataEx
import DSP.SignalMsg
import DSP.SignalRep
import DSP.TransporterCtrlUsesPort_v3POA
import DSP.iq
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterDataPortImpl.TransporterController
import ru.levkopo.barsik.emu.str

class TransporterCtrlUsesPortImpl(
    private val application: ApplicationImpl
) : TransporterCtrlUsesPort_v3POA() {
    override fun SendTest() {
        TODO("Not yet implemented")
    }

    override fun SendSignalMessage(message: SignalMsg) {
        println(message.str())
        val newMessage = SignalMsg(
            GenericSignalParams(
                0.0,
                0,
                0.0,
                0.0,
                0.0,
                message.params.width,
                message.params.filter,
                message.params.w,
                message.params.ae,
                message.params.af,
                message.params.octets,
            ),
            message.a,
            message.b,
            message.c,
            message.d,
            1,
            false,
            SignalDataEx(
                emptyArray(),
                emptyArray(),
                Array(message.params.width.toInt() / 1000) {
                    iq(0.06f, 0.06f)
                },
                PowerPhase(
                    byteArrayOf(),
                    byteArrayOf(),
                    1
                ),
//                SignalRep(
//                    true
//                )
            ),
            _orb().create_any().apply {
                insert_TypeCode(_orb().get_primitive_tc(TCKind.tk_null))
            }
        )

        println(newMessage.str())
        val port = application.getConnectedPort("DataConnection") as TransporterController
        port.sendSignalMessage(newMessage)
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