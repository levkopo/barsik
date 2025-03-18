package ru.levkopo.barsik.emu

import ru.levkopo.barsik.emu.modulators.BadAppleModulator
import ru.levkopo.barsik.emu.modulators.MicrophoneModulator
import ru.levkopo.barsik.emu.modulators.SampleModulator
import ru.levkopo.barsik.emu.modulators.base.BaseModulator

/**
 * Список модуляторов
 */
val modulators = listOf<BaseModulator>(
    SampleModulator(),
    MicrophoneModulator(),
    BadAppleModulator(),
)