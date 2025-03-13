package ru.levkopo.barsik.configs

object DetectorsConfig: BaseConfig() {
    var minAmplitude by doubleProperty(250.0)
}