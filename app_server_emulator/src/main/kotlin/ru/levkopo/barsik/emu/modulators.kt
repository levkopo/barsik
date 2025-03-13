package ru.levkopo.barsik.emu

import ru.levkopo.barsik.emu.modulators.MicrophoneModulator
import ru.levkopo.barsik.emu.modulators.SampleModulator
import ru.levkopo.barsik.emu.modulators.base.BaseModulator

val modulators = listOf<BaseModulator>(
    SampleModulator(),
    MicrophoneModulator()
)