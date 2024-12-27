package ru.levkopo.barsik.emu.poa.ports.transporters

import DSP.SignalMessage
import DSP.TransporterCtrlUsesPort_v1Helper
import DSP.TransporterCtrlUsesPort_v3POA
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl

class TransporterCtrlUsesPortImpl(
    private val application: ApplicationImpl
) : TransporterCtrlUsesPort_v3POA() {
    override fun SendTest() {
        TODO("Not yet implemented")
    }

    override fun SendPowerPhaseQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendIQSpectrumQuery(): Int {
        TODO("Not yet implemented")
    }

    override fun SendSignalMessage(message: SignalMessage) {
        println(with(message) {
            "SignalMessage{" +
                    "frequency=" + frequency +
                    ", attenuator=" + attenuator +
                    ", width=" + width +
                    ", signals_=" + signals_ +
                    ", aaa=" + aaa +
                    ", aab=" + aab +
                    ", aac=" + aac +
                    ", aad=" + aad +
                    ", packetNumber=" + packetNumber +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    ", f=" + f +
                    ", aa=" + aa +
                    ", ab=" + ab +
                    ", ac=" + ac +
                    ", ae=" + ae +
                    ", af=" + af +
                    ", ag=" + ag +
                    ", ah=" + ah +
                    '}'
        })

        val dataPort = TransporterCtrlUsesPort_v1Helper.unchecked_narrow(application.getConnectedPort("DataConnection"))
        dataPort.SendSignalMessage(
            SignalMessage(
                message.frequency,
                message.attenuator,
                message.width,
                message.signals_,
                message.aaa,
                message.aab,
                message.aac,
                message.aad,
                message.packetNumber + 1,
                message.b,
                message.c,
                message.d,
                message.f,
                message.aa,
                message.ab,
                message.ac,
                message.ae,
                message.af,
                message.ag,
                message.ah,
            )
        )
    }

    override fun getSigBoardInfo(): Int {
        TODO("Not yet implemented")
    }

    override fun releaseFifo() {
        println("TransporterCtrlUsesPort_v3POA.releaseFifo")
    }
}