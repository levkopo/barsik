package ru.levkopo.barsik.emu.poa.ports.transporters

import DSP.TransporterCtrlUsesPort_v3POA
import org.omg.PortableServer.POA

class TransporterCtrlUsesPortImpl(
    val rootPOA: POA
): TransporterCtrlUsesPort_v3POA() {
    override fun connectPort() {
        TODO("Not yet implemented")
    }

    override fun SendTest(): Int {
        TODO("Not yet implemented")
    }

    override fun SendPowerPhaseQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendIQSpectrumQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendSignalMessage(): Int {
        TODO("Not yet implemented")
    }

    override fun getSigBoardInfo(): Int {
        TODO("Not yet implemented")
    }

    override fun releaseFifo(): Int {
        TODO("Not yet implemented")
    }
}