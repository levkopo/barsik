package ru.levkopo.barsik.emu.poa.ports.resource

import CF.AbstractPort
import CF.DataType
import CF.PropertiesHolder
import CF.ResourcePOA
import DSP.AttenuatorSet
import DSP.SigBoardInfo3
import DSP.SigBoardInfo3Helper
import DSP.SigBoardInfo3Holder
import DSP.band_t
import com.sun.corba.se.impl.corba.AnyImpl
import org.omg.CORBA.AnyHolder
import org.omg.CORBA.TCKind
import ru.levkopo.barsik.emu.UnsafeUtils
import ru.levkopo.barsik.emu.UnsafeUtils.BYTES_OFFSET
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl


class ResourceImpl(
    private val application: ApplicationImpl
) : ResourcePOA() {

    val sig = SigBoardInfo3(
        "SigBoardInfo3",
        "SigBoardInfo3",
        "SigBoardInfo3",
        floatArrayOf(10.0f, 25.0f, 50.0f, 100.0f, 200.0f, 500.0f, 1000.0f, 2500.0f, 5000.0f, 10000.0f, 12500.0f,
                    25000.0f, 50000.0f, 100000.0f, 200000.0f, 250000.0f),
        floatArrayOf(
            468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 
            468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f
        ),
        floatArrayOf(
            5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 
            2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f
        ),
        floatArrayOf(
            0.2f, 1.0f, 0.0f, 0.08f, 0.4f, 0.0f, 0.04f, 0.2f, 0.0f, 0.02f, 0.1f, 0.0f, 0.01f, 0.05f, 0.0f, 0.004f, 
            0.02f, 0.0f, 0.002f, 0.01f, 0.0f, 8.0E-4f, 0.004f, 0.0f, 4.0E-4f, 0.002f, 0.0f, 2.0E-4f, 0.001f, 0.0f, 
            1.6E-4f, 8.0E-4f, 0.0f, 8.0E-5f, 4.0E-4f, 0.0f, 4.0E-5f, 2.0E-4f, 0.0f, 2.0E-5f, 1.0E-4f, 0.0f, 1.0E-5f, 
            5.0E-5f, 0.0f, 8.0E-6f, 4.0E-5f, 0.0f
        ),
        floatArrayOf(
            1.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f
        ),
        floatArrayOf(
            5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f,
            2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f
        ),
        AttenuatorSet(
            arrayOf(
                band_t(0, 2.0E7, 1.0E9),
                band_t(1, 1.0E9, 3.01E9),
            ),
            arrayOf()
        ),
        2.toShort(),
        2.0E7,
        3.01E9,
        floatArrayOf(
            5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f,
            2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f
        )
    )


    override fun query(o: PropertiesHolder) {
        SigBoardInfo3Helper.insert(o.value[0].value, sig)
    }

    override fun getPort(type: String): AbstractPort = application.getPort(type)

    override fun releaseObject() {
        println("Released resource")
        _this()._release()
    }

    fun swapBytes(bytes: ByteArray) {
        assert(bytes.size % 4 == 0)
        var i = 0L
        while (i < bytes.size) {
            UnsafeUtils.unsafe.putInt(
                bytes,
                BYTES_OFFSET + i,
                Integer.reverseBytes(UnsafeUtils.unsafe.getInt(bytes, BYTES_OFFSET + i))
            )
            i += 4
        }
    }
}