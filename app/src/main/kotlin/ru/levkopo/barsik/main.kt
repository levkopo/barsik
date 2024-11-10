package ru.levkopo.barsik

import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper


fun main(args: Array<String>) {
    val orb = ORB.init(args, null)
    val objRef = orb.resolve_initial_references("NameService")
    val ncRef = NamingContextExtHelper.narrow(objRef)

}