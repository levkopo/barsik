package ru.levkopo.barsik

import DSP.SignalMsg
import DSP.TransporterCtrlUsesPort_v3POA

class BarsikClientTransporterImpl(
    val collector: (SignalMsg) -> Unit,
    val testComplete: () -> Unit
): TransporterCtrlUsesPort_v3POA() {
    override fun SendTest() {
        testComplete()
    }

    override fun SendSignalMessage(message: SignalMsg) {
        println(message)
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
        TODO("Not yet implemented")
    }
}