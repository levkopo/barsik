package ru.levkopo.barsik.configs

object ServerConfig: BaseConfig() {
    var linuxBoxIp by stringProperty("10.8.0.5")
    var linuxBoxOrbPort by stringProperty("9999")
    var linuxBoxUsername by stringProperty("at")
    var linuxBoxPassword by stringProperty("gfhjkm")
    var linuxBoxSUPassword by stringProperty("rfhfcbr")
}