package ru.levkopo.barsik.configs

import java.util.*

object ORBConfig {
    fun buildOrbInitialParameters() = arrayOf(
        "-ORBInitialHost", ServerConfig.linuxBoxIp,
        "-ORBInitialPort", ServerConfig.linuxBoxOrbPort,
        "-ORBInitRef.NameService=corbaloc::${ServerConfig.linuxBoxIp}:${ServerConfig.linuxBoxOrbPort}/NameService"
    )

    fun buildOrbProperties() = Properties().apply {
        this["org.omg.CORBA.ORBInitialHost"] = ServerConfig.linuxBoxIp
        this["org.omg.CORBA.ORBInitialPort"] = ServerConfig.linuxBoxOrbPort

        System.setProperty("jacorb.log.default.verbosity", "0")
        this["org.omg.CORBA.ORBClass"] = "org.jacorb.orb.ORB"

        val clientConnTimeout = Integer.getInteger("com.lni.lps.clientConnTimeout", 60000)
        val clientReplyTimeout = Integer.getInteger("com.lni.lps.clientReplyTimeout", 300000)

        this["jacorb.connection.client.pending_reply_timeout"] = clientReplyTimeout.toString()
        this["jacorb.connection.client.connect_timeout"] = clientConnTimeout.toString()

        this["jacorb.connection.server.timeout"] = clientReplyTimeout.toString()
    }
}