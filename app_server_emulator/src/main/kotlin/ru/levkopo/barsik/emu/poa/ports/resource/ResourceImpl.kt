package ru.levkopo.barsik.emu.poa.ports.resource

import CF.AbstractPort
import CF.PropertiesHolder
import CF.ResourcePOA
import DSP.AttenuatorSet
import DSP.SigBoardInfo3
import DSP.SigBoardInfo3Helper
import DSP.band_t
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl

/**
 * Эмулятор ресурса с информацией о сигнальной плате
 */
class ResourceImpl(
    private val application: ApplicationImpl
) : ResourcePOA() {
    val sig = SigBoardInfo3(
        /* Идентификатор приемника */
        "SigBoardInfo3",
        /* Идентификатор соединения */
        "SigBoardInfo3",
        /* Идентификатор коммутатора */
        "SigBoardInfo3",
        /* Фильтры */
        floatArrayOf(
            10.0f, 25.0f, 50.0f, 100.0f, 200.0f, 500.0f, 1000.0f, 2500.0f, 5000.0f, 10000.0f, 12500.0f,
            25000.0f, 50000.0f, 100000.0f, 200000.0f, 250000.0f
        ),
        /* simpleFreq */
        floatArrayOf(
            468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f,
            468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f, 468750.0f
        ),
        /* rangeBands */
        floatArrayOf(
            5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f,
            2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f
        ),
        /* watchTimes */
        floatArrayOf(
            0.2f, 1.0f, 0.0f, 0.08f, 0.4f, 0.0f, 0.04f, 0.2f, 0.0f, 0.02f, 0.1f, 0.0f, 0.01f, 0.05f, 0.0f, 0.004f,
            0.02f, 0.0f, 0.002f, 0.01f, 0.0f, 8.0E-4f, 0.004f, 0.0f, 4.0E-4f, 0.002f, 0.0f, 2.0E-4f, 0.001f, 0.0f,
            1.6E-4f, 8.0E-4f, 0.0f, 8.0E-5f, 4.0E-4f, 0.0f, 4.0E-5f, 2.0E-4f, 0.0f, 2.0E-5f, 1.0E-4f, 0.0f, 1.0E-5f,
            5.0E-5f, 0.0f, 8.0E-6f, 4.0E-5f, 0.0f
        ),
        /* winParams */
        floatArrayOf(
            1.0f,
            2.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
        ),
        /* superRangeBands */
        floatArrayOf(
            5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f, 5000000.0f,
            2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f, 2.0E7f
        ),
        /* Информация об аттенюаторах и каналах */
        AttenuatorSet(
            arrayOf(
                band_t(0, 2.0E7, 1.0E9),
                band_t(1, 1.0E9, 3.01E9),
            ),
            arrayOf()
        ),
        /* Я не знаю что это, но это надо и даётся сервером (возможно количество каналов)*/
        2.toShort(),
        /* Минимальная частота */
        2.0E7,
        /* Максимальная частота */
        3.01E9,
        /* allGroupRangeBands */
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
}