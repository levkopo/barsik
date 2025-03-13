package ru.levkopo.barsik.emu.modulators.base

import DSP.iq

object ModulatorsManager {
    var currentIQ: Array<iq> = emptyArray()
    var carrierFrequency: Double = 0.0
}