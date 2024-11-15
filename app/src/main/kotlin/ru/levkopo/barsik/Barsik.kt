package ru.levkopo.barsik

import CF.*
import DSP.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.apache.commons.net.telnet.TelnetClient
import org.jacorb.orb.CDRInputStream
import org.omg.CORBA.ORB
import org.omg.CORBA.portable.ObjectImpl
import org.omg.CosNaming.NameComponent
import org.omg.CosNaming.NamingContextHelper
import org.omg.PortableServer.POAHelper
import java.io.PrintStream
import java.util.*

class Barsik {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = TelnetClient().apply {
        connectTimeout = 5000
    }

    private val _state = MutableStateFlow<State>(State.IDLE)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _transporter = MutableStateFlow<TransporterCtrlUsesPort_v2?>(null)
    val transporter: StateFlow<TransporterCtrlUsesPort_v2?> = _transporter.asStateFlow()

    private val orbProperties = Properties().apply {
        this["org.omg.CORBA.ORBInitialHost"] = Config.LINUX_BOX_IP
        this["org.omg.CORBA.ORBInitialPort"] = Config.LINUX_BOX_ORB_PORT

        System.setProperty("jacorb.log.default.verbosity", "0")
        this["org.omg.CORBA.ORBClass"] = "org.jacorb.orb.ORB"

        val clientConnTimeout = Integer.getInteger("com.lni.lps.clientConnTimeout", 60000)
        val clientReplyTimeout = Integer.getInteger("com.lni.lps.clientReplyTimeout", 300000)

        this["jacorb.connection.client.pending_reply_timeout"] = clientReplyTimeout.toString()
        this["jacorb.connection.client.connect_timeout"] = clientConnTimeout.toString()

        this["jacorb.connection.server.timeout"] = clientReplyTimeout.toString()
    }


    fun start() {
        _state.value = State.CONNECTING
        scope.launch {
            runCatching {
                client.connect(Config.LINUX_BOX_IP)
                delay(200)

                val outputStream = PrintStream(client.outputStream)
                outputStream.println(Config.LINUX_BOX_USER)
                outputStream.flush()
                delay(200)

                outputStream.println(Config.LINUX_BOX_PASSWORD)
                outputStream.flush()
                delay(200)

                outputStream.println("su\r\n")
                outputStream.flush()
                delay(200)

                outputStream.println(Config.LINUX_BOX_SU_PASSWORD)
                outputStream.flush()
                delay(200)

                outputStream.println("./uhf.sh")
                outputStream.flush()
            }.onSuccess {
                runCatching {
                    val orb = ORB.init(
                        arrayOf(
                            "-ORBInitialHost", Config.LINUX_BOX_IP,
                            "-ORBInitialPort", Config.LINUX_BOX_ORB_PORT.toString(),
                            "-ORBSupportBootstrapAgent", "1",
                            "-ORBInitRef.NameService=corbaloc::${Config.LINUX_BOX_IP}:${Config.LINUX_BOX_ORB_PORT}/NameService"
                        ),
                        orbProperties
                    )

                    val poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
                    poa.the_POAManager().activate()

                    val obj = orb.resolve_initial_references("NameService")
                    val ncRef = NamingContextHelper.narrow(obj)

                    val components = arrayOf(
                        NameComponent("DSP", ""),
                        NameComponent("NIG-5 Applications", ""),
                    )

                    val factory = ApplicationFactoryHelper.narrow(ncRef.resolve(components))

                    val os = (factory as ObjectImpl)._request("create", true)
                    byteArrayOf(
                        0x07, 0x00, 0x00, 0x00, 0x53, 0x4e, 0x54, 0x65, 0x73, 0x74,
                        0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00,
                        0x70, 0x72, 0x6f, 0x66, 0x69, 0x6c, 0x65, 0x00, 0x12, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x15, 0x00, 0x00, 0x00,
                        0x61, 0x63, 0x65, 0x6e, 0x74, 0x65, 0x72, 0x2e, 0x63, 0x6f,
                        0x6e, 0x66, 0x2f, 0x73, 0x61, 0x64, 0x2e, 0x78, 0x6d, 0x6c,
                        0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
                        0x00
                    ).forEach { os.write_octet(it) }

                    val application = ApplicationHelper.read(factory._invoke(os))
//                    val application = factory.create("SNTest", AppConfig("test", "profile", "acenter.conf/sad.xml"))
//                    println(application.getPort("Scheduler").ip)
                }.onFailure {
                    it.printStackTrace()
                    _state.value = State.ERROR(it)
                }.onSuccess {
                    _state.value = State.CONNECTED
                }
            }.onFailure {
                it.printStackTrace()
                _state.value = State.ERROR(it)
            }
        }
    }

    fun stop() {
        _state.value = State.IDLE
        client.disconnect()
    }

    sealed interface State {
        data object IDLE : State
        data object CONNECTING : State
        data object CONNECTED : State
        data class ERROR(val error: Throwable) : State
    }
}