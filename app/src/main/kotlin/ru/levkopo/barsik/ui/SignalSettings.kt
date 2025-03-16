package ru.levkopo.barsik.ui

import kotlinx.coroutines.flow.MutableStateFlow

object SignalSettings {
    val graphScale = MutableStateFlow<Scale>(Scale.DBM)
    val detectorAmplitude = MutableStateFlow<Double>(250.0)

    enum class Scale(
        val title: String,
    ) {
        DBM("дБм"),
        MICRO_VOLT("мкВ"),
    }
}