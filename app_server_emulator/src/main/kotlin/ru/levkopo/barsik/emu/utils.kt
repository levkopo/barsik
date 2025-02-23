package ru.levkopo.barsik.emu

import org.omg.CORBA.Object
import org.omg.CosNaming.NameComponent
import org.omg.CosNaming.NamingContext
import org.omg.CosNaming.NamingContextOperations

fun NamingContextOperations.try_bind_new_context(n: Array<NameComponent>) {
    try {
        bind_new_context(n)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun NamingContextOperations.bind_or_rebind_context(n: Array<NameComponent>, nc: NamingContext) {
    try {
        bind_context(n, nc)
    } catch (e: Exception) {
        e.printStackTrace()
        rebind_context(n, nc)
    }
}

fun NamingContextOperations.bind_or_rebind(n: Array<NameComponent?>?, obj: Object?) {
    try {
        bind(n, obj)
    } catch (e: Exception) {
        rebind(n, obj)
    }
}
