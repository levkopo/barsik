package ru.levkopo.barsik.data.remote

import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContext
import org.omg.CosNaming.NamingContextHelper
import org.omg.PortableServer.POA
import org.omg.PortableServer.POAHelper
import ru.levkopo.barsik.configs.ORBConfig
import ru.levkopo.barsik.data.repositories.LogsRepository

/**
 * Логика подключения к серверу ORB
 */
internal class OrbManager {
    private val telnetInitializer = TelnetInitializer()

    lateinit var orb: ORB
        private set

    lateinit var poa: POA
        private set

    lateinit var namingContext: NamingContext
        private set

    suspend fun initialize(): Boolean {
        if(!telnetInitializer.initialize()) {
            return false
        }

        try {
            LogsRepository.info(javaClass.simpleName, "Инициализация ORB")
            orb = ORB.init(
                arrayOf(
                    "-ORBSupportBootstrapAgent", "1",
                    "-ORBSupportBootstrapAgent", "1",
                ) + ORBConfig.buildOrbInitialParameters(),
                ORBConfig.buildOrbProperties()
            )

            LogsRepository.info(javaClass.simpleName, "Получение NamingContext")
            namingContext = NamingContextHelper.narrow(orb.resolve_initial_references("NameService"))

            LogsRepository.info(javaClass.simpleName, "Активация POA")
            poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
            poa.the_POAManager().activate()

            LogsRepository.info(javaClass.simpleName, "Успешная инициализация")
            return true
        }catch (e: Throwable) {
            LogsRepository.error(javaClass.simpleName, e)
            return false
        }
    }
}