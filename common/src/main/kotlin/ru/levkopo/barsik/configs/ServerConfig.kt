package ru.levkopo.barsik.configs

/**
 * Класс конфигурации, содержащий информацию о подключении к серверу
 */
object ServerConfig: BaseConfig() {
    var linuxBoxIp by stringProperty("10.66.23.100")
    var linuxBoxOrbPort by stringProperty("9999")
    var linuxBoxUsername by stringProperty("at")
    var linuxBoxPassword by stringProperty("gfhjkm")
    var linuxBoxSUPassword by stringProperty("rfhfcbr")
}