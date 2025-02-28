package ru.levkopo.barsik.emu.modulators

import DSP.iq

interface BaseModulator {
    val currentIQ: Array<iq>
    var carrierFrequency: Double

    fun start()
    fun stop()
}