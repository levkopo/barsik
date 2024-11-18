package ru.levkopo.barsik.emu.poa.factory

import CF.*
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl

class ApplicationFactoryImpl(
    val rootPOA: org.omg.PortableServer.POA,
) : ApplicationFactoryPOA() {
    override fun create(appConfig: AppConfig): Application {
        println("Creating a factory: " +
                "task: ${appConfig.task}, " +
                "a: ${appConfig.a}, " +
                "b: ${appConfig.b}, " +
                "c: ${appConfig.c}, " +
                "path: ${appConfig.path}"
        )

        val servant = ApplicationImpl(rootPOA, appConfig)
        rootPOA.activate_object(servant)
        return ApplicationHelper.narrow(rootPOA.servant_to_reference(servant))
    }
}