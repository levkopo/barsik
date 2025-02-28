package ru.levkopo.barsik.configs

object ApplicationConfig: BaseConfig() {
    var profile by stringProperty("acenter.conf/sad.xml")
}