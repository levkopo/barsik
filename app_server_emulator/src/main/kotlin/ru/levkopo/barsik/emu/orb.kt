package ru.levkopo.barsik.emu

import CF.ApplicationFactoryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper
import org.omg.PortableServer.POAHelper
import ru.levkopo.barsik.configs.ORBConfig
import ru.levkopo.barsik.emu.poa.factory.ApplicationFactoryImpl
import ru.levkopo.barsik.emu.utils.bind_or_rebind
import ru.levkopo.barsik.emu.utils.try_bind_new_context
import java.util.*
import kotlin.system.exitProcess

private val mainScope = CoroutineScope(Dispatchers.IO)
private val orbdProcessFlow = MutableStateFlow<Process?>(null)
private val runtime = Runtime.getRuntime()

const val JVM18_PATH = "/home/levkopo/.jdks/liberica-full-1.8.0_442"

val orbFlow = MutableStateFlow<ORB?>(null)

fun launchORBD() = mainScope.launch {
    orbdProcessFlow.value?.destroyForcibly()
    orbdProcessFlow.value = null

    val orbdStartup = arrayOf("$JVM18_PATH/bin/orbd") + ORBConfig.buildOrbInitialParameters()
    println("Starting ORB daemon: ${orbdStartup.joinToString(" ")}")

    val process = runtime.exec(orbdStartup)
    val scanner = Scanner(process.inputStream)

    mainScope.launch {
        while (scanner.hasNextLine()) {
            val output = scanner.nextLine()
            println("ORBD: $output")
        }
    }

    delay(1000)
    if (process.isAlive) {
        orbdProcessFlow.value = process
    } else {
        System.err.println("ORB daemon exited with ${process.waitFor()}")
        exitProcess(1)
    }
}

fun launchORBObserver() = mainScope.launch {
    orbdProcessFlow.filterNotNull().collect {
        val orbServerLock = Mutex()

        println("Starting CORBA...")

        orbServerLock.withLock {
            val orb = ORB.init(
                arrayOf(
                    "-ORBSupportBootstrapAgent", "1",
                ) + ORBConfig.buildOrbInitialParameters(),
                Properties().apply {
                    put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB")
                    put("org.omg.CORBA.ORBSingletonClass", "org.jacorb.orb.ORBSingleton")
                }
            )

            val rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))!!
            rootPoa.the_POAManager().activate()

            val poa = rootPoa.create_POA("AppFactoryPOA", rootPoa.the_POAManager(), arrayOf())
            val factory = ApplicationFactoryImpl(poa)
            poa.activate_object(factory)
            val factoryRef = poa.servant_to_reference(factory)

            val objRef = orb.resolve_initial_references("NameService")
            val namingContext = NamingContextExtHelper.narrow(objRef)

            namingContext.try_bind_new_context(namingContext.to_name("DSP"))
            namingContext.bind_or_rebind(
                namingContext.to_name("DSP/NIG-5 Applications"),
                ApplicationFactoryHelper.narrow(factoryRef)
            )

            orbFlow.value = orb
            orb.run()
        }
    }
}
