package ru.levkopo.barsik.emubind

import CF.Application
import CF.ApplicationHelper
import DSP.ApplicationFactoryPOA
import ru.levkopo.barsik.emu.ApplicationImpl

class ApplicationFactoryPOAImpl(val rootPOA: org.omg.PortableServer.POA): ApplicationFactoryPOA() {
    override fun create(a: Char, b: String?, profile: String?): Application {
        val servant = ApplicationImpl()
        rootPOA.activate_object(servant)
        return ApplicationHelper.narrow(rootPOA.servant_to_reference(servant))
    }
}