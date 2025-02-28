package ru.levkopo.barsik.models

import DSP.SignalMsg
import DSP.iq
import DSP.short_iq

fun SignalMsg.asString(): String = with(this) {
    "SignalMsg{\n" +
            "params=" + with(params) {
        "GenericSignalParams{\n" +
                "freq=" + freq +
                ",\n attenuator=" + attenuator +
                ",\n c=" + c +
                ",\n d=" + d +
                ",\n f=" + f +
                ",\n width=" + width +
                ",\n filter=" + filter +
                ",\n qualityPhase=" + qualityPhase +
                ",\n ae=" + ae +
                ",\n channel=" + channel +
                ",\n octets=" + octets.contentToString() +
                '}'
    } +
            ",\n packet=" + packetNumber +
            ",\n b=" + b +
            ",\n c=" + c +
            ",\n d=" + d +
            ",\n dd=" + dd +
            ",\n ddd=" + ddd +
            "\nextedned=" + with(extended) {
        "SignalDataEx{" +
                "a=" + a.joinToString { it.asString() } +
                ",\n b=" + b.joinToString { it.asString() } +
                ",\n c=" + c.size +
                ", c=" + c.joinToString { it.asString() } +
                with(power) {
                    "\nPowerPhase{" +
                            "a=" + angles.contentToString() +
                            ", b=" + b.contentToString() +
                            ", c=" + c +
                            '}'
                } +
                with(rep) {
                    "\nSignalRep{" +
                            "test=" + test +
                            '}'
                } +
                '}'
    } +
            "\n ${data.type()}" +
            '}'
}

fun iq.asString(): String = with(this) {
    "iq(${i}f, ${q}f)"
}

fun short_iq.asString(): String = with(this) {
    "siq{$i:$q}"
}