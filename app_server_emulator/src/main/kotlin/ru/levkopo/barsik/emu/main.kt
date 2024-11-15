package ru.levkopo.barsik.emu

import DSP.ApplicationFactoryHelper
import com.khubla.telnet.TelnetServer
import com.khubla.telnet.nvt.NVT
import com.khubla.telnet.nvt.spy.NVTSpy
import com.khubla.telnet.nvt.spy.impl.ConsoleNVTSpyImpl
import com.khubla.telnet.shell.Shell
import com.khubla.telnet.shell.ShellFactory
import com.khubla.telnet.shell.basic.BasicTelnetCommandRegistryImpl
import org.omg.CosNaming.NamingContextExtHelper
import org.omg.PortableServer.POAHelper
import ru.levkopo.barsik.Config
import ru.levkopo.barsik.emubind.ApplicationFactoryPOAImpl
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

private val hostParameters = arrayOf(
    "-ORBInitialPort", Config.LINUX_BOX_ORB_PORT.toString(),
    "-ORBInitialHost", Config.LINUX_BOX_IP,
)

private val clients = mutableListOf<BarsClientSocket>()

class BarsClientSocket(
    val socket: Socket
): Runnable {
    val inputStream = socket.getInputStream()
    val outputStream = socket.getInputStream()

    init {
        thread(block = this::run)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun run() {
        while (socket.isConnected) {
            val byte = inputStream.read()
            print("new input" + byte.toHexString())
        }
    }
}

fun main() {
    thread {
        println("Starting broker...")
        val runtime = Runtime.getRuntime()
        runtime.exec(arrayOf(System.getProperty("java.home") + "/bin/orbd") + hostParameters)
        sleep(1000L)

        println("Starting CORBA...")
        val props = Properties()
        props.setProperty("jacorb.implname", "BarsikSerber")
        props.setProperty("OAPort", Config.LINUX_BOX_ORB_PORT.toString())

        val orb = org.omg.CORBA.ORB.init(
            arrayOf(
                "-ORBInitRef.NameService=corbaloc::localhost:${Config.LINUX_BOX_ORB_PORT}/NameService",
            ) + hostParameters,
            props
        )

        val rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))!!
        rootPoa.the_POAManager().activate()

        val poa = rootPoa.create_POA("AppFactoryPOA", rootPoa.the_POAManager(), arrayOf())
        val factory = ApplicationFactoryPOAImpl(poa)
        poa.activate_object(factory)
        val factoryRef = poa.servant_to_reference(factory)

        val objRef = orb.resolve_initial_references("NameService")
        val namingContext = NamingContextExtHelper.narrow(objRef)

        namingContext.bind_or_rebind_context(namingContext.to_name("DSP"), namingContext)
        namingContext.bind_or_rebind(
            namingContext.to_name("DSP/NIG-5 Applications"),
            ApplicationFactoryHelper.narrow(factoryRef)
        )

        orb.run()
    }

//    thread {
//        val socketServer = ServerSocket(Config.LINUX_BOX_TCP_PORT)
//        while (socketServer.isBound) {
//            clients.add(BarsClientSocket(socketServer.accept()))
//            println("New client")
//        }
//
//        socketServer.close()
//    }

    TelnetServer(23, 20, object : ShellFactory {
        override fun createShell(nvt: NVT): Shell = BarsShellImpl(
            nvt,
            BasicTelnetCommandRegistryImpl()
        ) { login, password, _ ->
            println("login: $login, password: $password")
            true
        }

        override fun getNVTSpy(): NVTSpy = ConsoleNVTSpyImpl()
    }).run()
}