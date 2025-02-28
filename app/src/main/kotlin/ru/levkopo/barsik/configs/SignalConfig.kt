package ru.levkopo.barsik.configs

object SignalConfig: BaseConfig() {
    var frequency by doubleProperty(3.43E8)
    var attenuator by shortProperty(24)
    var c by doubleProperty(1800000.0)
    var d by doubleProperty(1.2600225506490411E-257)
    var f by doubleProperty(4.195177201736488E-308)
    var width by doubleProperty(1200000.0)
    var filter by floatProperty(2500.0f)
    var qualityPhase by floatProperty(0.004f)
    var ae by intProperty(0)
    var channel by intProperty(1)
}