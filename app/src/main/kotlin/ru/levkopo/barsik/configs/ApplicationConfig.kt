package ru.levkopo.barsik.configs

/**
 * Конфигурация создания и запуска приложения сервера
 */
object ApplicationConfig: BaseConfig() {
    /**
     * Скрипт для запуска по telnet
     */
    var serverStartupCommandName by stringProperty("./uhf.sh")

    /**
     * Профиль, содержащий информацию о приложении на сервере
     */
    var profile by stringProperty("acenter.conf/sad.xml")
}