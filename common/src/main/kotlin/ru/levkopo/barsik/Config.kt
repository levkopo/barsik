package ru.levkopo.barsik

import java.util.*

object Config {
    const val LINUX_BOX_IP = "10.66.23.100"
    const val LINUX_BOX_ORB_PORT = 9999
    const val LINUX_BOX_USER = "at"
    const val LINUX_BOX_PASSWORD = "gfhjkm"
    const val LINUX_BOX_SU_PASSWORD = "rfhfcbr"

    val orbInitialParameters = arrayOf(
        "-ORBInitialHost", LINUX_BOX_IP,
        "-ORBInitialPort", LINUX_BOX_ORB_PORT.toString(),
        "-ORBInitRef.NameService=corbaloc::$LINUX_BOX_IP:$LINUX_BOX_ORB_PORT/NameService"
    )

    val orbProperties = Properties().apply {
        this["org.omg.CORBA.ORBInitialHost"] = LINUX_BOX_IP
        this["org.omg.CORBA.ORBInitialPort"] = LINUX_BOX_ORB_PORT

        System.setProperty("jacorb.log.default.verbosity", "0")
        this["org.omg.CORBA.ORBClass"] = "org.jacorb.orb.ORB"

        val clientConnTimeout = Integer.getInteger("com.lni.lps.clientConnTimeout", 60000)
        val clientReplyTimeout = Integer.getInteger("com.lni.lps.clientReplyTimeout", 300000)

        this["jacorb.connection.client.pending_reply_timeout"] = clientReplyTimeout.toString()
        this["jacorb.connection.client.connect_timeout"] = clientConnTimeout.toString()

        this["jacorb.connection.server.timeout"] = clientReplyTimeout.toString()
    }

}