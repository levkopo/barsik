package ru.levkopo.barsik.configs

object ServerConfig: BaseConfig() {
    var linuxBoxIp by stringProperty("10.66.23.100")
    var linuxBoxOrbPort by stringProperty("9999")
    var linuxBoxUsername by stringProperty("at")
    var linuxBoxPassword by stringProperty("gfhjkm")
    var linuxBoxSUPassword by stringProperty("rfhfcbr")
}