package ru.levkopo.barsik.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Настройки отображения и детектора сигналов
 */
object SignalSettings {
    /**
     * Настройка используемой шкалы
     */
    val graphScale = MutableStateFlow<Scale>(Scale.DBM)

    /**
     * Минимальная амплитуда детектора
     */
    val detectorAmplitude = MutableStateFlow<Double>(250.0)

    /**
     * Максимальная зафиксированная амплитуда
     */
    var maxAmplitude = 0.0

    /**
     * Минимальная зафиксированная амплитуда
     */
    var minAmplitude = 0.0

    init {
        // Сброс амплитуды при изменении шкалы
        CoroutineScope(Dispatchers.IO).launch {
            graphScale.collectLatest {
                maxAmplitude = 0.0
                minAmplitude = 0.0
            }
        }
    }

    enum class Scale(
        val title: String,
    ) {
        DBM("дБм"),
        MICRO_VOLT("мкВ"),
    }
}