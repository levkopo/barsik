package ru.levkopo.barsik.data.remote

import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContext
import org.omg.CosNaming.NamingContextHelper
import org.omg.PortableServer.POA
import org.omg.PortableServer.POAHelper
import ru.levkopo.barsik.configs.ORBConfig

internal class OrbManager {
    private val telnetInitializer = TelnetInitializer()

    lateinit var orb: ORB
        private set

    lateinit var poa: POA
        private set

    lateinit var namingContext: NamingContext
        private set

    suspend fun initialize() = telnetInitializer.initialize().onSuccess {
        runCatching {
            orb = ORB.init(
                arrayOf(
                    "-ORBSupportBootstrapAgent", "1",
                    "-ORBSupportBootstrapAgent", "1",
                ) + ORBConfig.buildOrbInitialParameters(),
                ORBConfig.buildOrbProperties()
            )

            namingContext = NamingContextHelper.narrow(orb.resolve_initial_references("NameService"))

            poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
            poa.the_POAManager().activate()
        }
    }
}