package ru.levkopo.barsik.emu.poa.application

import CF.*
import CF.PortSupplier.UnknownPort
import DSP.*
import org.omg.IOP.IORHelper
import org.omg.PortableServer.POA
import ru.levkopo.barsik.emu.poa.ports.resource.ResourceImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterCtrlUsesPortImpl
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterDataPortImpl

class ApplicationImpl(
    val rootPOA: POA,
    val appConfig: AppConfig,
) : ApplicationPOA() {
    private val connectedPorts = HashMap<String, AbstractPort>()
    /*
1,
1,
1,
intArrayOf(),
intArrayOf(),
intArrayOf(),
intArrayOf(),
intArrayOf(),

intArrayOf(),
//        yOf(
//            MapEntry("IdRcv", 1),
//            MapEntry("IdCon", 1),
////            MapEntry("IdSwt", 12),
////            MapEntry("Filters", 1),
////            MapEntry("SampleFreqs", 1),
////            MapEntry("RangeBands", 1),
////            MapEntry("WatchTimes", 1),
////            MapEntry("WinParams", 1),
////            MapEntry("SuperRangeBands", 1),
//        ),
//        AttenuatorSet()
 */

    private val sigBoardInfoPOA = object : SigBoardInfo3 {
        override fun IdRcv(): Int = 1
        override fun IdCon(): Int = 1
        override fun IdSwt(): Int = 1

        override fun filters(): IntArray = intArrayOf(0)
        override fun simpleFreq(): IntArray = intArrayOf(0)
        override fun rangeBands(): IntArray = intArrayOf(0)
        override fun watchTimes(): IntArray = intArrayOf(0)
        override fun winParams(): IntArray = intArrayOf(0)
        override fun attenuator(): AttenuatorSet = AttenuatorSet(
            arrayOf(
                band_t(
                    30000000, 1000000001,
                    arrayOf(
                        att_entry_t(
                            1,
                            5,
                            1,
                            30000000,
                            1000000001
                        )
                    )
                )
            )
        )

        override fun allGroupRangeBands(): IntArray = intArrayOf(0)
    }

    fun getConnectedPort(type: String) = connectedPorts[type]
    fun connectPort(type: String, port: AbstractPort) {
        connectedPorts[type] = port
    }

    internal fun getSigBoardInfo(): SigBoardInfo3 {
        return sigBoardInfoPOA
    }

    override fun getPort(type: String): AbstractPort {
        println("Required port: $type")
        val (servant, narrow) = when (type) {
            "Scheduler" -> ResourceImpl(this) to ResourceHelper::narrow
            "TransporterCtrlPort" -> TransporterCtrlUsesPortImpl(this) to TransporterCtrlUsesPort_v3Helper::narrow
            "TransporterDataPort" -> TransporterDataPortImpl(this) to TransporterDataPortHelper::narrow
            else -> throw UnknownPort("Unknown port: $type")
        }

        rootPOA.activate_object(servant)
        return narrow(rootPOA.servant_to_reference(servant))
    }

    override fun releaseObject() {
        println("Application released")
        _this()._release()
    }
}