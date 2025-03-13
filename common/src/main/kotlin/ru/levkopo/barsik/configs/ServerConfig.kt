package ru.levkopo.barsik.configs

object ServerConfig: BaseConfig() {
    var linuxBoxIp by stringProperty("192.168.2.219")
    var linuxBoxOrbPort by stringProperty("9999")
    var linuxBoxUsername by stringProperty("at")
    var linuxBoxPassword by stringProperty("gfhjkm")
    var linuxBoxSUPassword by stringProperty("rfhfcbr")
}