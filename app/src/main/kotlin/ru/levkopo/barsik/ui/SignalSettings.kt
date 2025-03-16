package ru.levkopo.barsik.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object SignalSettings {
    val graphScale = MutableStateFlow<Scale>(Scale.DBM)
    val detectorAmplitude = MutableStateFlow<Double>(250.0)
    var maxAmplitude = 0.0
    var minAmplitude = 0.0

    init {
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