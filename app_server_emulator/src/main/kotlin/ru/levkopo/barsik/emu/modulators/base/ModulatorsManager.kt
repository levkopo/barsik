package ru.levkopo.barsik.emu.modulators.base

import DSP.iq

/**
 * Менеджер, хранящий в себе обработанные сигналы модулятором и частоту
 */
object ModulatorsManager {
    /**
     * IQ сигналы, обработанные модулятором
     */
    var currentIQ: Array<iq> = emptyArray()

    /**
     * Частота, на которой происходит модуляция
     */
    var carrierFrequency: Double = 0.0
}