package ru.levkopo.barsik.emu.utils

import sun.misc.Unsafe
import java.lang.reflect.Field

object UnsafeUtils {
    val unsafe by lazy {
        val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.setAccessible(true)
        f.get(null as Any?) as Unsafe
    }

    val BYTES_OFFSET by lazy { unsafe.arrayBaseOffset(ByteArray::class.java).toLong() }
}