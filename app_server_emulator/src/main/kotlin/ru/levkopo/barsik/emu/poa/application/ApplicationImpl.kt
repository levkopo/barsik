package ru.levkopo.barsik.emu.poa.application

import CF.*
import CF.PortSupplier.UnknownPort
import DSP.*
import org.omg.CORBA.Object
import org.omg.IOP.IORHelper
import org.omg.PortableServer.POA
import ru.levkopo.barsik.emu.poa.ports.resource.ResourceImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterCtrlUsesPortImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterDataPortImpl

/**
 * Эмулятор серверного приложения
 */
class ApplicationImpl(
    private val rootPOA: POA,
    private val initConfiguration: Array<out DataType>,
    private val deviceAssignments: Array<out DeviceAssignmentType>
) : ApplicationPOA() {
    private val connectedPorts = HashMap<String, Any>()

    init {
        println("Created application: initConfiguration${initConfiguration}: ${initConfiguration.joinToString { "${it.id} - ${it.value.type()} - ${it.value.extract_string()}" }}")
        println("deviceAssignments${deviceAssignments.size}: ${deviceAssignments.joinToString { "${it.componentId} - ${it.assignedDeviceId}" }}")
    }

    fun getConnectedPort(type: String) = connectedPorts[type]
    fun connectPort(connection: Any, connectionId: String) {
        connectedPorts[connectionId] = connection
    }

    override fun getPort(type: String): AbstractPort {
        println("Required port: $type")
        val (servant, narrow) = when (type) {
            "Scheduler" -> ResourceImpl(this) to ResourceHelper::narrow
            "DFOutConnection" -> ResourceImpl(this) to ResourceHelper::narrow
            "TransporterCtrlPort" -> TransporterCtrlUsesPortImpl(this) to TransporterCtrlUsesPort_v3Helper::narrow
            "TransporterDataPort" -> TransporterDataPortImpl(this) to TransporterDataPortHelper::narrow
            else -> throw UnknownPort("Unknown port: $type")
        }

        rootPOA.activate_object(servant)
        return narrow(rootPOA.servant_to_reference(servant))
    }

    override fun releaseObject() {
        println("Application released")
        _this()._release()
    }
}