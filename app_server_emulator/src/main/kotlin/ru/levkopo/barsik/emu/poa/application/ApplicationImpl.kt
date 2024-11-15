package ru.levkopo.barsik.emu.poa.application

import CF.ApplicationPOA
import CF.Port
import CF.PortSupplier.UnknownPort
import CF.ResourceHelper
import DSP.TransporterCtrlUsesPort_v3Helper
import DSP.TransporterDataPortHelper
import org.omg.PortableServer.POA
import ru.levkopo.barsik.emu.poa.ports.resource.ResourceImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterCtrlUsesPortImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterDataPortImpl

class ApplicationImpl(
    val rootPOA: POA,
    val task: String,
    val a: Int,
    val b: Int,
    val path: String
) : ApplicationPOA() {
    override fun getPort(type: String): Port {
        println("Required port: $type")
        val (servant, narrow) = when (type) {
            "Scheduler" -> ResourceImpl(rootPOA) to ResourceHelper::narrow
            "TransporterCtrlPort" -> TransporterCtrlUsesPortImpl(rootPOA) to TransporterCtrlUsesPort_v3Helper::narrow
            "TransporterDataPort" -> TransporterDataPortImpl(rootPOA) to TransporterDataPortHelper::narrow
            else -> throw UnknownPort("Unknown port: $type")
        }

        rootPOA.activate_object(servant)
        return narrow(rootPOA.servant_to_reference(servant))
    }

    override fun releaseObject() {
        println("Application released")
    }
}