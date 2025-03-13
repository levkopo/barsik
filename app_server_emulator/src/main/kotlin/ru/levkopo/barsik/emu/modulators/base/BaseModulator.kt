package ru.levkopo.barsik.emu.modulators.base

import DSP.iq
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.levkopo.barsik.emu.modulators.base.ModulatorsManager

abstract class BaseModulator {
    internal var currentIQ: Array<iq>
        get() = ModulatorsManager.currentIQ
        set(value) {
            ModulatorsManager.currentIQ = value
        }

    internal val carrierFrequency: Double
        get() = ModulatorsManager.carrierFrequency

    private var job: Job? = null

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    internal abstract suspend fun run()

    @Composable
    abstract fun SettingsScreen()

    fun start() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            _isRunning.value = true

            runCatching {
                run()
            }.onFailure {
                it.printStackTrace()
            }

            _isRunning.value = false
        }
    }

    fun stop() {
        job?.cancel()
        _isRunning.value = false
    }
}