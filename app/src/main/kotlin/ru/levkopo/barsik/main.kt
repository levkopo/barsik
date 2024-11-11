package ru.levkopo.barsik

import DSP.TransporterCtrlUsesPort_v2Helper
import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper
import org.omg.PortableServer.POAHelper
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread
import kotlin.emptyArray

const val JACORB = "JACORB"


fun main() {
    val props = Properties()
    props["org.omg.CORBA.ORBInitialHost"] = "10.66.23.100"
    props["org.omg.CORBA.ORBInitialPort"] = "9999"

    var orbName = System.getProperty("com.lni.lps.orb")
    if (orbName == null) {
        orbName = JACORB
    }

    if (orbName == JACORB) {
        System.setProperty("jacorb.log.default.verbosity", "0")
        props["org.omg.CORBA.ORBClass"] = "org.jacorb.orb.ORB"

        val clientConnTimeout = Integer.getInteger("com.lni.lps.clientConnTimeout", 60000)
        val clientReplyTimeout = Integer.getInteger("com.lni.lps.clientReplyTimeout", 300000)

        props["jacorb.connection.client.pending_reply_timeout"] = clientReplyTimeout.toString()
        props["jacorb.connection.client.connect_timeout"] = clientConnTimeout.toString()

        props["jacorb.connection.server.timeout"] = clientReplyTimeout.toString()
    }

    val orb = ORB.init(emptyArray(), props)

    try {
        while (true) {
            try {
                val poa = POAHelper.unchecked_narrow(orb.resolve_initial_references("RootPOA"))
                poa.the_POAManager().activate()

                val ncRef = NamingContextExtHelper.unchecked_narrow(orb.resolve_initial_references("NameService"))
                break
            }catch (e: Exception) {}
        }
    } finally {
        println("destroy")
        orb.destroy()
    }

//    val ctrl = TransporterCtrlUsesPort_v2Helper.unchecked_narrow(ncRef)
//
//
//    println(ctrl.SendTest())
//    println(ctrl.SendSignalMessage())
}