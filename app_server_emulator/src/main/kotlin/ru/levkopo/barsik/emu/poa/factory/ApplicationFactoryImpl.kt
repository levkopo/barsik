package ru.levkopo.barsik.emu.poa.factory

import CF.*
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl

class ApplicationFactoryImpl(
    private val rootPOA: org.omg.PortableServer.POA,
) : ApplicationFactoryPOA() {
    override fun create(
        name: String,
        initConfiguration: Array<out DataType>,
        deviceAssignments: Array<out DeviceAssignmentType>
    ): Application {
        val servant = ApplicationImpl(rootPOA, initConfiguration, deviceAssignments)
        rootPOA.activate_object(servant)
        return ApplicationHelper.narrow(rootPOA.servant_to_reference(servant))
    }
}