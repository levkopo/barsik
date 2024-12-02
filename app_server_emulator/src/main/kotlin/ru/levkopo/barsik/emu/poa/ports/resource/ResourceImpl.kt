package ru.levkopo.barsik.emu.poa.ports.resource

import CF.AbstractPort
import CF.Port
import CF.ResourcePOA
import DSP.SigBoardInfo3
import org.omg.CORBA.portable.InputStream
import org.omg.CORBA.portable.OutputStream
import org.omg.CORBA.portable.ResponseHandler
import ru.levkopo.barsik.emu.UnsafeUtils
import ru.levkopo.barsik.emu.UnsafeUtils.BYTES_OFFSET
import ru.levkopo.barsik.emu.poa.application.ApplicationImpl
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ResourceImpl(val applicationImpl: ApplicationImpl) : ResourcePOA() {
    override fun _invoke(method: String?, _input: InputStream?, handler: ResponseHandler): OutputStream {
        if (method == "query") {
            val data =
                "010000000e000000536967426f617264496e666f330030000f0000005c030000010000001a00000049444c3a4453502f536967426f617264496e666f333a312e300000000e000000536967426f617264496e666f330000000e0000000600000049645263760000001200000000000000060000004964436f6e000000120000000000000006000000496453777400000012000000000000000800000046696c7465727300130000000c0000000100000006000000000000000c00000053616d706c65467265717300130000000c0000000100000006000000000000000b00000052616e676542616e64730000130000000c0000000100000006000000000000000b000000576174636854696d65730000130000000c0000000100000006000000000000000a00000057696e506172616d73000000130000000c00000001000000060000000000000010000000537570657252616e676542616e647300130000000c0000000100000006000000000000000c000000417474656e7561746f7273000f00000094010000010000001a00000049444c3a4453502f417474656e7561746f725365743a312e300000000e000000417474656e7561746f72536574000000020000000600000062616e64730000001500000098000000010000001400000049444c3a4453502f42616e645365713a312e30000800000042616e64536571001300000068000000010000000f00000058000000010000001300000049444c3a4453502f62616e645f743a312e3000000700000062616e645f740000030000000300000069640000030000000800000062656766726571000700000008000000656e64667265710007000000000000000700000076616c75657300001500000098000000010000001800000049444c3a4453502f417474456e7472795365713a312e30000c000000417474456e747279536571001300000060000000010000000f00000050000000010000001800000049444c3a4453502f6174745f656e7472795f743a312e30000c0000006174745f656e7472795f7400020000000800000062616e645f69640003000000040000006174740007000000000000000a0000004368616e436f756e7400000002000000080000006d696e467265710007000000080000006d617846726571000700000013000000416c6c47726f757052616e676542616e64730000130000000c00000001000000060000000000000001000000000000000100000000000000010000000000000010000000000020410000c841000048420000c842000048430000fa4300007a4400401c4500409c4500401c46005043460050c346005043470050c347005043480024744810000000c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b30000000cdcc4c3e0000803f000000000ad7a33dcdcccc3e000000000ad7233dcdcc4c3e000000000ad7a33ccdcccc3d000000000ad7233ccdcc4c3d000000006f12833b0ad7a33c000000006f12033b0ad7233c0000000017b7513a6f12833b0000000017b7d1396f12033b0000000017b751396f12833a00000000acc5273917b7513a00000000acc5a73817b7d13900000000acc5273817b7513900000000acc5a73717b7d13800000000acc5273717b7513800000000bd370637acc5273800000000300000000000803f0000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b02000000000000000000000000000000d01273410000000065cdcd4101000000000000000000000065cdcd41000000901e6de6410b000000000000000000000000000000000000000000000000000000000024400000000000000000000000000000344000000000000000000000000000003e4001000000000000000000000000000000010000000000000000000000000014400100000000000000000000000000244001000000000000000000000000002e40010000000000000000000000000034400100000000000000000000000000394001000000000000000000000000003e40020000000000000000000000d0127341000000901e6de641100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b"
                    .chunked(2)
                    .map { it.toInt(16).toByte() }
                    .toByteArray()

            swapBytes(data)

            val reply = handler.createReply()
            reply.write_octet_array(data, 0, data.size)
            return reply
        }

        return super._invoke(method, _input, handler)
    }

    override fun query(a: Int, type: String): SigBoardInfo3 = applicationImpl.getSigBoardInfo()

    override fun getPort(type: String): AbstractPort = applicationImpl.getPort(type)

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