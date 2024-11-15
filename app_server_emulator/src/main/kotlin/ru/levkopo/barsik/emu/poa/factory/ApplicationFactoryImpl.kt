package ru.levkopo.barsik.emu.poa.factory

import CF.*
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl

class ApplicationFactoryImpl(val rootPOA: org.omg.PortableServer.POA) : ApplicationFactoryPOA() {
    override fun create(
        task: String,
        a: Int,
        type: String,
        b: Int,
        path: String
    ): Application {
        println("Creating a factory: task: $task, a: $a, b: $b, path: $path")

        val servant = ApplicationImpl(rootPOA, task, a, b, path)
        rootPOA.activate_object(servant)
        return ApplicationHelper.narrow(rootPOA.servant_to_reference(servant))
    }
}