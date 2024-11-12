package ru.levkopo.barsik

import DSP.TransporterCtrlUsesPort_v2
import DSP.TransporterCtrlUsesPort_v2Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.net.telnet.TelnetClient
import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper
import org.omg.PortableServer.POAHelper
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

                val scanner = Scanner(client.inputStream)
                val outputStream = client.outputStream.writer()
                var tryingSu = false
                while (true) {
                    val line = scanner.nextLine()
                    when {
                        line.startsWith("box login:") -> outputStream.write(Config.LINUX_BOX_USER + "\r\n")
                        line.startsWith("Password:") -> outputStream.write(
                            when (tryingSu) {
                                true -> Config.LINUX_BOX_SU_PASSWORD
                                else -> Config.LINUX_BOX_PASSWORD
                            } + "\r\n"
                        )

                        line.endsWith("$") -> {
                            if (!tryingSu) {
                                tryingSu = true
                                outputStream.write("su\r\n")
                            }
                        }

                        line.endsWith("#") -> {
                            outputStream.write("./uhf\r\n")
                            break
                        }
                        else -> {
                            client.disconnect()
                            throw Error("Unknown answer: $line")
                        }
                    }
                }
            }.onSuccess {
                runCatching {
                    val orb = ORB.init(emptyArray(), orbProperties)

                    val poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
                    poa.the_POAManager().activate()

                    val ncRef =
                        NamingContextExtHelper.unchecked_narrow(orb.resolve_initial_references("NameService"))
                    TransporterCtrlUsesPort_v2Helper.unchecked_narrow(ncRef)
                }.onFailure {
                    _state.value = State.ERROR(it)
                }.onSuccess {
                    _state.value = State.CONNECTED(it)
                }
            }.onFailure {
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
        data class CONNECTED(val transporter: TransporterCtrlUsesPort_v2) : State
        data class ERROR(val error: Throwable) : State
    }
}