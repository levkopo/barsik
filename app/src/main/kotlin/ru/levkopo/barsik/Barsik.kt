package ru.levkopo.barsik

import CF.*
import DSP.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.apache.commons.net.telnet.TelnetClient
import org.omg.CORBA.ORB
import org.omg.CosNaming.NameComponent
import org.omg.CosNaming.NamingContextHelper
import org.omg.PortableServer.POAHelper
import java.io.PrintStream

class Barsik {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = TelnetClient().apply {
        connectTimeout = 5000
    }

    private val _connectionStateFlow = MutableStateFlow<ConnectionState>(ConnectionState.IDLE)
    val connectionStateFlow: StateFlow<ConnectionState> = _connectionStateFlow.asStateFlow()

    private val _transporterFlow = MutableStateFlow<TransporterCtrlUsesPort_v2?>(null)
    val transporterFlow: StateFlow<TransporterCtrlUsesPort_v2?> = _transporterFlow.asStateFlow()

    private var packetCount = 1
    private var frequency = 0.0
    private var attenuator = 0.0
    private var width = 0.0
    private val clientTransporter = BarsikClientTransporterImpl(
        collector = { message ->
            println(message.makeString())
            packetCount = message.packetNumber + 1
            sendSignalMessage()
        },
        testComplete = {
            _connectionStateFlow.value = ConnectionState.CONNECTED
        }
    )

    private fun sendSignalMessage() {
        val transporter = transporterFlow.value!!
        println("")

//        transporter.SendSignalMessage(
//            SignalMessage(
//                4.4262274E-317,
//                0.0,
//                3.6476116E-317,
//                Signals(1374923778, 143957360, 22095344, 3025572, 0, 1094616192, 1148846080, 1008981770),
//                0,
//                1,
//                1,
//                8,
//                packetCount,
//                false,
//                1,
//                true,
//                false,
//                0.0,
//                0.0,
//                0.0,
//                0,
//                186,
//                1,
//                1
//            )
//        )
    }

    fun startSend(
        frequency: Double,
        attenuator: Double,
        width: Double,
    ) {
        this.frequency = frequency
        this.attenuator = attenuator
        this.width = width * 1000

        packetCount = 1
        sendSignalMessage()
    }

    fun connect() {
        _connectionStateFlow.value = ConnectionState.CONNECTING
        scope.launch {
            runCatching {
                client.connect(Config.LINUX_BOX_IP)
                delay(100)

                val outputStream = PrintStream(client.outputStream)
                outputStream.println(Config.LINUX_BOX_USER)
                outputStream.flush()
                delay(100)

                outputStream.println(Config.LINUX_BOX_PASSWORD)
                outputStream.flush()
                delay(100)

                outputStream.println("su\r\n")
                outputStream.flush()
                delay(100)

                outputStream.println(Config.LINUX_BOX_SU_PASSWORD)
                outputStream.flush()
                delay(100)

                outputStream.println("./uhf.sh")
                outputStream.flush()

                delay(1000)
            }.onSuccess {
                runCatching {
                    val orb = ORB.init(
                        arrayOf(
                            "-ORBSupportBootstrapAgent", "1",
                        ) + Config.orbInitialParameters,
                        Config.orbProperties
                    )

                    val poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
                    poa.the_POAManager().activate()
                    poa.activate_object(clientTransporter)

                    val obj = orb.resolve_initial_references("NameService")
                    val ncRef = NamingContextHelper.narrow(obj)

                    val components = arrayOf(
                        NameComponent("DSP", ""),
                        NameComponent("NIG-5 Applications", ""),
                    )

                    val factory = ApplicationFactoryHelper.narrow(ncRef.resolve(components))
                    val application = factory.create(
                        AppConfig(
                            "SNTest",
                            1,
                            "profile",
                            18,
                            0,
                            "acenter.conf/sad.xml",
                            0,
                            0,
                            0
                        )
                    )


                    val scheduler = ResourceHelper.narrow(application.getPort("Scheduler"))

                    _transporterFlow.value =
                        TransporterCtrlUsesPort_v2Helper.narrow(scheduler.getPort("TransporterCtrlPort"))

//                    val transporterDataPort =
//                        PortHelper.narrow(scheduler.getPort("TransporterDataPort"))
//                    transporterDataPort.connectPort(
//                        PortHelper.unchecked_narrow(
//                            poa.servant_to_reference(
//                                clientTransporter
//                            )
//                        ),
//                        0,
//                        1,
//                        36,
//                        1,
//                        1,
//                        1,
//                        20,
//                        1,
//                        65537,
//                        0,
//                        65801,
//                        0,
//                        "DataConnection"
//                    )
                }.onFailure {
                    it.printStackTrace()
                    _connectionStateFlow.value = ConnectionState.ERROR(it)
                }
            }.onFailure {
                it.printStackTrace()
                _connectionStateFlow.value = ConnectionState.ERROR(it)
            }
        }
    }

    fun disconnect() {
        _transporterFlow.value!!._get_orb().shutdown(false)
        _connectionStateFlow.value = ConnectionState.IDLE
        client.disconnect()
    }

    sealed interface ConnectionState {
        data object IDLE : ConnectionState
        data object CONNECTING : ConnectionState
        data object CONNECTED : ConnectionState
        data class ERROR(val error: Throwable) : ConnectionState
    }
}