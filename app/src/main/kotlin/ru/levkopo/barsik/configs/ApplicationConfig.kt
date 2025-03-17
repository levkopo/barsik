package ru.levkopo.barsik.configs

object ApplicationConfig: BaseConfig() {
    var serverStartupCommandName by stringProperty("./uhf.sh")
    var profile by stringProperty("acenter.conf/sad.xml")
}