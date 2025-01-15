package ru.levkopo.barsik.emu.poa.ports.resource

import CF.AbstractPort
import CF.PropertiesHolder
import CF.ResourcePOA
import DSP.AttenuatorSet
import DSP.SigBoardInfo3
import DSP.SigBoardInfo3Helper
import DSP.SigBoardInfo3Holder
import DSP.band_t
import ru.levkopo.barsik.emu.UnsafeUtils
import ru.levkopo.barsik.emu.UnsafeUtils.BYTES_OFFSET
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl


class ResourceImpl(
    private val application: ApplicationImpl
) : ResourcePOA() {

    val sig = SigBoardInfo3(
        1, 0, 1,
        intArrayOf(), intArrayOf(0),
        intArrayOf(
            1092616192, 1103626240, 1112014848, 1120403456, 1128792064, 1140457472,
            1148846080, 1159479296, 1167867904, 1176256512, 1178816512, 1187205120,
            1195593728, 1203982336, 1212370944, 1215570944
        ),
        intArrayOf(
            1222959552, 1222959552, 1222959552, 1222959552, 1222959552, 1222959552,
            1222959552, 1222959552, 1222959552, 1222959552, 1222959552, 1222959552,
            1222959552, 1222959552, 1222959552, 1222959552
        ),
        intArrayOf(
            1251513984, 1251513984, 1251513984, 1251513984, 1251513984, 1268291200,
            1268291200, 1268291200, 1268291200, 1268291200, 1268291200, 1268291200,
            1268291200, 1268291200, 1268291200, 1268291200
        ),
        AttenuatorSet(
            arrayOf(
                band_t(1045220557, 1065353216, 0),
                band_t(1034147594, 1053609165, 0),
                band_t(1025758986, 1045220557, 0),
                band_t(1017370378, 1036831949, 0),
                band_t(1008981770, 1028443341, 0),
                band_t(998445679, 1017370378, 0),
                band_t(990057071, 1008981770, 0),
                band_t(978433815, 998445679, 0),
                band_t(970045207, 990057071, 0),
                band_t(961656599, 981668463, 0),
                band_t(958907820, 978433815, 0),
                band_t(950519212, 970045207, 0),
                band_t(942130604, 961656599, 0),
                band_t(933741996, 953267991, 0),
                band_t(925353388, 944879383, 0),
                band_t(923154365, 942130604, 0),
                band_t(48, 1065353216, 1073741824),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 0, 0),
                band_t(0, 16, 1251513984),
                band_t(
                    1251513984, 1251513984,
                    1251513984
                ),
                band_t(
                    1251513984, 1268291200,
                    1268291200
                ),
                band_t(
                    1268291200, 1268291200,
                    1268291200
                ),
                band_t(
                    1268291200, 1268291200,
                    1268291200
                ),
                band_t(
                    1268291200, 1268291200,
                    1268291200
                ),
                band_t(2, 0, 1098060496),
                band_t(0, 1104006501, 0),
                band_t(1, 0, 1104006501),
                band_t(0, 1105620254, -1879048192),
                band_t(11, 0, 0),
                band_t(0, 0, 0),
                band_t(1076101120, 0, 0),
                band_t(0, 1077149696, 0),
                band_t(0, 0, 1077805056),
                band_t(0, 1, 0)
            ),
            arrayOf()
        ),
        intArrayOf()
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