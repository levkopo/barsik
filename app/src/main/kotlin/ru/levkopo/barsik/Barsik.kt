package ru.levkopo.barsik

import CF.*
import DSP.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.apache.commons.net.ProtocolCommandEvent
import org.apache.commons.net.ProtocolCommandListener
import org.apache.commons.net.telnet.TelnetClient
import org.omg.CORBA.ORB
import org.omg.CosNaming.NameComponent
import org.omg.CosNaming.NamingContextHelper
import org.omg.PortableServer.POAHelper
import java.io.PrintStream
import kotlin.concurrent.thread

class Barsik {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = TelnetClient().apply {
        connectTimeout = 5000
    }

    private val _connectionStateFlow = MutableStateFlow<ConnectionState>(ConnectionState.IDLE)
    val connectionStateFlow: StateFlow<ConnectionState> = _connectionStateFlow.asStateFlow()

    private val _transporterFlow = MutableStateFlow<TransporterCtrlUsesPort?>(null)
    val transporterFlow: StateFlow<TransporterCtrlUsesPort?> = _transporterFlow.asStateFlow()

    private var orb: ORB? = null

    private var packetCount = 1
    private var frequency = 0.0
    private var attenuator = 0.0
    private var width = 0.0
    private val clientTransporter = BarsikClientTransporterImpl(
        collector = { message ->
            sendSignalMessage()
        },
        testComplete = {
            _connectionStateFlow.value = ConnectionState.CONNECTED
        }
    )

    private fun sendSignalMessage() {
        val transporter = transporterFlow.value!!
//        val any = orb!!.create_any()
//        val outputStream = any.create_output_stream()
//        "0000007910cfb94100000000000000000000000060e3464102acf351c8243a05f0255101a42a2e000000000080843e4100007a440ad7233c000000000100000001000000080000000100000000000000010000000100000000000000000000000000000000000000000000000000000000000000ba0000000100000001000000"
//            .chunked(2)
//            .map { it.toInt(16).toByte() }
//            .toByteArray()
//            .forEach {
//                outputStream.write_octet(it)
//            }
//
//        transporter._request("SendSignalMessage").apply {
//
//        }
//
//        transporter.SendSignalMessage()

//        transporter.SendSignalMessage(
////            SignalMessage(
////                4.4262274E-317,
////                0.0,
////                3.6476116E-317,
////                Signals(1374923778, 143957360, 22095344, 3025572, 0, 1094616192, 1148846080, 1008981770),
////                0,
////                1,
////                1,
////                8,
////                packetCount,
////                false,
////                1,
////                true,
////                false,
////                0.0,
////                0.0,
////                0.0,
////                0,
////                186,
////                1,
////                1
////            )
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

                client.disconnect()
            }.onSuccess {
                runCatching {
                    val orb = ORB.init(
                        arrayOf(
                            "-ORBSupportBootstrapAgent", "1",
                            "-ORBSupportBootstrapAgent", "1",
                        ) + Config.orbInitialParameters,
                        Config.orbProperties
                    )

                    this@Barsik.orb = orb

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
                    val profile = orb.create_any()
                    profile.insert_string("acenter.conf/sad.xml")

                    val application = factory.create(
                        "SNTest",
                        arrayOf(
                            DataType("profile", profile)
                        ),
                        arrayOf()
                    )


                    val scheduler = ResourceHelper.narrow(application.getPort("Scheduler"))

                    val systemInfo = orb.create_any()
                    val result = PropertiesHolder(
                        arrayOf(
                            DataType("SigBoardInfo3", systemInfo),
                        )
                    )

                    scheduler.query(result)

                    println("${SigBoardInfo3Helper.extract(result.value[0].value)}")

                    _transporterFlow.value =
                        TransporterCtrlUsesPortHelper.narrow(scheduler.getPort("TransporterCtrlPort"))

                    val transporterDataPort =
                        PortHelper.narrow(scheduler.getPort("TransporterDataPort"))
                    transporterDataPort.connectPort(
                        PortHelper.unchecked_narrow(
                            poa.servant_to_reference(
                                clientTransporter
                            )
                        ),
                        "DataConnection"
                    )
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
//        _transporterFlow.value!!._get_orb().shutdown(false)
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