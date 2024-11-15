package ru.levkopo.barsik.emu

import org.omg.CORBA.Object
import org.omg.CosNaming.NameComponent
import org.omg.CosNaming.NamingContext
import org.omg.CosNaming.NamingContextOperations
import org.omg.CosNaming.NamingContextPackage.AlreadyBound
import org.omg.CosNaming.NamingContextPackage.CannotProceed
import org.omg.CosNaming.NamingContextPackage.InvalidName
import org.omg.CosNaming.NamingContextPackage.NotFound

fun NamingContextOperations.bind_or_rebind_context(n: Array<NameComponent>, nc: NamingContext) {
    try {
        bind_context(n, nc)
    } catch (e: AlreadyBound) {
        rebind_context(n, nc)
    }
}

fun NamingContextOperations.bind_or_rebind(n: Array<NameComponent?>?, obj: Object?) {
    try {
        bind(n, obj)
    } catch (e: AlreadyBound) {
        rebind(n, obj)
    }
}
