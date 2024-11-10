package ru.levkopo.barsik

import DSP.*
import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper


fun main() {
    val args = arrayOf(
        "-ORBInitialPort",
        "9999",
        "-ORBInitialHost",
        "10.66.23.100"
    )

    val orb = ORB.init(args, null)
    val objRef = orb.resolve_initial_references("NameService")
    val ncRef = NamingContextExtHelper.narrow(objRef)

    val ctrl = TransporterCtrlUsesPort_v2Helper.narrow(ncRef)

    println(ctrl.SendTest())
    println(ctrl.SendSignalMessage())
}