package ru.levkopo.barsik.emu.poa.ports.transporters

import DSP.SignalMessage
import DSP.TransporterCtrlUsesPort_v3POA
import org.omg.PortableServer.POA

class TransporterCtrlUsesPortImpl(
    val rootPOA: POA
): TransporterCtrlUsesPort_v3POA() {
    override fun SendTest() {
        TODO("Not yet implemented")
    }

    override fun SendPowerPhaseQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendIQSpectrumQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendSignalMessage(message: SignalMessage?) {
//        println("SendSignalMessage: $frequency, $attenuator, $width")
//        signals.value = intArrayOf(0, 2, 5, 10, 5, 2)
//        _orb()
    }

    override fun getSigBoardInfo(): Int {
        TODO("Not yet implemented")
    }

    override fun releaseFifo(): Int {
        TODO("Not yet implemented")
    }
}