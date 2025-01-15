package ru.levkopo.barsik

import DSP.SignalMessage
import DSP.TransporterCtrlUsesPort_v3POA

class BarsikClientTransporterImpl(
    val collector: (SignalMessage) -> Unit,
    val testComplete: () -> Unit
): TransporterCtrlUsesPort_v3POA() {
    override fun SendTest() {
        testComplete()
    }

    override fun SendSignalMessage(message: SignalMessage) {
        collector(message)
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