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
import ru.levkopo.barsik.Config
import ru.levkopo.barsik.emu.poa.factory.ApplicationFactoryImpl
import java.util.*
import kotlin.system.exitProcess

private val mainScope = CoroutineScope(Dispatchers.IO)
private val orbdProcessFlow = MutableStateFlow<Process?>(null)
private val runtime = Runtime.getRuntime()

val orbFlow = MutableStateFlow<ORB?>(null)

fun launchORBD() = mainScope.launch {
    orbdProcessFlow.value?.destroyForcibly()
    orbdProcessFlow.value = null

    val orbdStartup = arrayOf(System.getProperty("java.home") + "/bin/orbd") + Config.orbInitialParameters
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
                ) + Config.orbInitialParameters,
                Properties()
            )

            val rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))!!
            rootPoa.the_POAManager().activate()

            val poa = rootPoa.create_POA("AppFactoryPOA", rootPoa.the_POAManager(), arrayOf())
            val factory = ApplicationFactoryImpl(poa)
            poa.activate_object(factory)
            val factoryRef = poa.servant_to_reference(factory)

            val objRef = orb.resolve_initial_references("NameService")
            val namingContext = NamingContextExtHelper.narrow(objRef)

            namingContext.bind_or_rebind_context(namingContext.to_name("DSP"), namingContext)
            namingContext.bind_or_rebind(
                namingContext.to_name("DSP/NIG-5 Applications"),
                ApplicationFactoryHelper.narrow(factoryRef)
            )

            orbFlow.value = orb
            orb.run()
        }
    }
}
