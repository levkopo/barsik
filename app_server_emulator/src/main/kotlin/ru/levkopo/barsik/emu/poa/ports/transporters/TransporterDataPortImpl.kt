package ru.levkopo.barsik.emu.poa.ports.transporters

import CF.AbstractPort
import CF.Port
import CF.PortSupplier.UnknownPort
import DSP.*
import org.omg.PortableServer.POA
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl
import kotlin.concurrent.thread

class TransporterDataPortImpl(
    private val application: ApplicationImpl
) : TransporterDataPortPOA() {
    override fun connectPort(
        port: AbstractPort,
        type: String
    ) {
        println("Connecting port: $port, type: $type")
        application.connectPort(type, port)
        when(type) {
            "DataConnection" -> when {
                port._is_a(TransporterCtrlUsesPort_v1Helper.id()) -> {
                    val transporter: TransporterController = try {
                        TransporterControllerV3(TransporterCtrlUsesPort_v3Helper.narrow(port))
                    } catch (_: Exception) {
                        try {
                            TransporterControllerV2(TransporterCtrlUsesPort_v2Helper.narrow(port))
                        } catch (_: Exception) {
                            TransporterControllerV1(TransporterCtrlUsesPort_v1Helper.unchecked_narrow(port))
                        }
                    }

                    println("Connected new port ${transporter.name}")
                    thread {
                        if (!port._non_existent()) {
                            println("Send test signal")
                            transporter.sendTest()
                        }
                    }
                }

                else -> throw UnknownPort()
            }
            else -> throw UnknownPort()
        }
    }

    open class TransporterControllerV1(
        val orbTransporter: TransporterCtrlUsesPort_v1
    ) : TransporterController {
        override val name: String = "transporter controller v1"
        override fun sendTest(): Unit = orbTransporter.SendTest()
    }


    open class TransporterControllerV2(
        val orbTransporter2: TransporterCtrlUsesPort_v2
    ) : TransporterControllerV1(orbTransporter2) {
        override val name: String = "transporter controller v2"
    }

    open class TransporterControllerV3(
        val orbTransporter3: TransporterCtrlUsesPort_v3
    ) : TransporterControllerV2(orbTransporter3) {
        override val name: String = "transporter controller v3"

    }


    interface TransporterController {
        val name: String
        fun sendTest()
    }
}