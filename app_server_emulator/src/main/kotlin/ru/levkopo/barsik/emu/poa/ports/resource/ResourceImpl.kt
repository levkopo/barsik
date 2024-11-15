package ru.levkopo.barsik.emu.poa.ports.resource

import CF.ResourcePOA
import org.omg.CORBA.portable.InputStream
import org.omg.CORBA.portable.OutputStream
import org.omg.CORBA.portable.ResponseHandler
import org.omg.PortableServer.POA

class ResourceImpl(val rootPOA: POA): ResourcePOA() {
    override fun _invoke(method: String, _input: InputStream, handler: ResponseHandler): OutputStream {
        println("Released called $method")

        if(method == "query") {
            println("Released query")

            val bytes =
                "10000000e000000536967426f617264496e666f330030000f0000005c030000010000001a00000049444c3a4453502f536967426f617264496e666f333a312e300000000e000000536967426f617264496e666f330000000e0000000600000049645263760000001200000000000000060000004964436f6e000000120000000000000006000000496453777400000012000000000000000800000046696c7465727300130000000c0000000100000006000000000000000c00000053616d706c65467265717300130000000c0000000100000006000000000000000b00000052616e676542616e64730000130000000c0000000100000006000000000000000b000000576174636854696d65730000130000000c0000000100000006000000000000000a00000057696e506172616d73000000130000000c00000001000000060000000000000010000000537570657252616e676542616e647300130000000c0000000100000006000000000000000c000000417474656e7561746f7273000f00000094010000010000001a00000049444c3a4453502f417474656e7561746f725365743a312e300000000e000000417474656e7561746f72536574000000020000000600000062616e64730000001500000098000000010000001400000049444c3a4453502f42616e645365713a312e30000800000042616e64536571001300000068000000010000000f00000058000000010000001300000049444c3a4453502f62616e645f743a312e3000000700000062616e645f740000030000000300000069640000030000000800000062656766726571000700000008000000656e64667265710007000000000000000700000076616c75657300001500000098000000010000001800000049444c3a4453502f417474456e7472795365713a312e30000c000000417474456e747279536571001300000060000000010000000f00000050000000010000001800000049444c3a4453502f6174745f656e7472795f743a312e30000c0000006174745f656e7472795f7400020000000800000062616e645f69640003000000040000006174740007000000000000000a0000004368616e436f756e7400000002000000080000006d696e467265710007000000080000006d617846726571000700000013000000416c6c47726f757052616e676542616e64730000130000000c00000001000000060000000000000001000000000000000100000000000000010000000000000010000000000020410000c841000048420000c842000048430000fa4300007a4400401c4500409c4500401c46005043460050c346005043470050c347005043480024744810000000c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448c0e1e448100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b30000000cdcc4c3e0000803f000000000ad7a33dcdcccc3e000000000ad7233dcdcc4c3e000000000ad7a33ccdcccc3d000000000ad7233ccdcc4c3d000000006f12833b0ad7a33c000000006f12033b0ad7233c0000000017b7513a6f12833b0000000017b7d1396f12033b0000000017b751396f12833a00000000acc5273917b7513a00000000acc5a73817b7d13900000000acc5273817b7513900000000acc5a73717b7d13800000000acc5273717b7513800000000bd370637acc5273800000000300000000000803f0000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b02000000000000000000000000000000d01273410000000065cdcd4101000000000000000000000065cdcd41000000901e6de6410b000000000000000000000000000000000000000000000000000000000024400000000000000000000000000000344000000000000000000000000000003e4001000000000000000000000000000000010000000000000000000000000014400100000000000000000000000000244001000000000000000000000000002e40010000000000000000000000000034400100000000000000000000000000394001000000000000000000000000003e40020000000000000000000000d0127341000000901e6de641100000008096984a8096984a8096984a8096984a8096984a8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b8096984b"
            val result = intArrayOf(0x1, 0x0, 0x0, 0x0, 0xe, 0x0, 0x0, 0x0, 0x53, 0x69, 0x67, 0x42, 0x6f, 0x61, 0x72, 0x64, 0x49, 0x6e, 0x66, 0x6f, 0x33, 0x0, 0x30, 0x0, 0xf, 0x0, 0x0, 0x0, 0x5c, 0x3, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x1a, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x53, 0x69, 0x67, 0x42, 0x6f, 0x61, 0x72, 0x64, 0x49, 0x6e, 0x66, 0x6f, 0x33, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0x0, 0x0, 0xe, 0x0, 0x0, 0x0, 0x53, 0x69, 0x67, 0x42, 0x6f, 0x61, 0x72, 0x64, 0x49, 0x6e, 0x66, 0x6f, 0x33, 0x0, 0x0, 0x0, 0xe, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x49, 0x64, 0x52, 0x63, 0x76, 0x0, 0x0, 0x0, 0x12, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x49, 0x64, 0x43, 0x6f, 0x6e, 0x0, 0x0, 0x0, 0x12, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x49, 0x64, 0x53, 0x77, 0x74, 0x0, 0x0, 0x0, 0x12, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x46, 0x69, 0x6c, 0x74, 0x65, 0x72, 0x73, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x53, 0x61, 0x6d, 0x70, 0x6c, 0x65, 0x46, 0x72, 0x65, 0x71, 0x73, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xb, 0x0, 0x0, 0x0, 0x52, 0x61, 0x6e, 0x67, 0x65, 0x42, 0x61, 0x6e, 0x64, 0x73, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xb, 0x0, 0x0, 0x0, 0x57, 0x61, 0x74, 0x63, 0x68, 0x54, 0x69, 0x6d, 0x65, 0x73, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xa, 0x0, 0x0, 0x0, 0x57, 0x69, 0x6e, 0x50, 0x61, 0x72, 0x61, 0x6d, 0x73, 0x0, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x53, 0x75, 0x70, 0x65, 0x72, 0x52, 0x61, 0x6e, 0x67, 0x65, 0x42, 0x61, 0x6e, 0x64, 0x73, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x41, 0x74, 0x74, 0x65, 0x6e, 0x75, 0x61, 0x74, 0x6f, 0x72, 0x73, 0x0, 0xf, 0x0, 0x0, 0x0, 0x94, 0x1, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x1a, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x41, 0x74, 0x74, 0x65, 0x6e, 0x75, 0x61, 0x74, 0x6f, 0x72, 0x53, 0x65, 0x74, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0x0, 0x0, 0xe, 0x0, 0x0, 0x0, 0x41, 0x74, 0x74, 0x65, 0x6e, 0x75, 0x61, 0x74, 0x6f, 0x72, 0x53, 0x65, 0x74, 0x0, 0x0, 0x0, 0x2, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x62, 0x61, 0x6e, 0x64, 0x73, 0x0, 0x0, 0x0, 0x15, 0x0, 0x0, 0x0, 0x98, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x14, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x42, 0x61, 0x6e, 0x64, 0x53, 0x65, 0x71, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0x8, 0x0, 0x0, 0x0, 0x42, 0x61, 0x6e, 0x64, 0x53, 0x65, 0x71, 0x0, 0x13, 0x0, 0x0, 0x0, 0x68, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0xf, 0x0, 0x0, 0x0, 0x58, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x62, 0x61, 0x6e, 0x64, 0x5f, 0x74, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0x0, 0x7, 0x0, 0x0, 0x0, 0x62, 0x61, 0x6e, 0x64, 0x5f, 0x74, 0x0, 0x0, 0x3, 0x0, 0x0, 0x0, 0x3, 0x0, 0x0, 0x0, 0x69, 0x64, 0x0, 0x0, 0x3, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x62, 0x65, 0x67, 0x66, 0x72, 0x65, 0x71, 0x0, 0x7, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x65, 0x6e, 0x64, 0x66, 0x72, 0x65, 0x71, 0x0, 0x7, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7, 0x0, 0x0, 0x0, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x73, 0x0, 0x0, 0x15, 0x0, 0x0, 0x0, 0x98, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x18, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x41, 0x74, 0x74, 0x45, 0x6e, 0x74, 0x72, 0x79, 0x53, 0x65, 0x71, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0xc, 0x0, 0x0, 0x0, 0x41, 0x74, 0x74, 0x45, 0x6e, 0x74, 0x72, 0x79, 0x53, 0x65, 0x71, 0x0, 0x13, 0x0, 0x0, 0x0, 0x60, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0xf, 0x0, 0x0, 0x0, 0x50, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x18, 0x0, 0x0, 0x0, 0x49, 0x44, 0x4c, 0x3a, 0x44, 0x53, 0x50, 0x2f, 0x61, 0x74, 0x74, 0x5f, 0x65, 0x6e, 0x74, 0x72, 0x79, 0x5f, 0x74, 0x3a, 0x31, 0x2e, 0x30, 0x0, 0xc, 0x0, 0x0, 0x0, 0x61, 0x74, 0x74, 0x5f, 0x65, 0x6e, 0x74, 0x72, 0x79, 0x5f, 0x74, 0x0, 0x2, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x62, 0x61, 0x6e, 0x64, 0x5f, 0x69, 0x64, 0x0, 0x3, 0x0, 0x0, 0x0, 0x4, 0x0, 0x0, 0x0, 0x61, 0x74, 0x74, 0x0, 0x7, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xa, 0x0, 0x0, 0x0, 0x43, 0x68, 0x61, 0x6e, 0x43, 0x6f, 0x75, 0x6e, 0x74, 0x0, 0x0, 0x0, 0x2, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x6d, 0x69, 0x6e, 0x46, 0x72, 0x65, 0x71, 0x0, 0x7, 0x0, 0x0, 0x0, 0x8, 0x0, 0x0, 0x0, 0x6d, 0x61, 0x78, 0x46, 0x72, 0x65, 0x71, 0x0, 0x7, 0x0, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0x41, 0x6c, 0x6c, 0x47, 0x72, 0x6f, 0x75, 0x70, 0x52, 0x61, 0x6e, 0x67, 0x65, 0x42, 0x61, 0x6e, 0x64, 0x73, 0x0, 0x0, 0x13, 0x0, 0x0, 0x0, 0xc, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x6, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x0, 0x0, 0x20, 0x41, 0x0, 0x0, 0xc8, 0x41, 0x0, 0x0, 0x48, 0x42, 0x0, 0x0, 0xc8, 0x42, 0x0, 0x0, 0x48, 0x43, 0x0, 0x0, 0xfa, 0x43, 0x0, 0x0, 0x7a, 0x44, 0x0, 0x40, 0x1c, 0x45, 0x0, 0x40, 0x9c, 0x45, 0x0, 0x40, 0x1c, 0x46, 0x0, 0x50, 0x43, 0x46, 0x0, 0x50, 0xc3, 0x46, 0x0, 0x50, 0x43, 0x47, 0x0, 0x50, 0xc3, 0x47, 0x0, 0x50, 0x43, 0x48, 0x0, 0x24, 0x74, 0x48, 0x10, 0x0, 0x0, 0x0, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0xc0, 0xe1, 0xe4, 0x48, 0x10, 0x0, 0x0, 0x0, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x30, 0x0, 0x0, 0x0, 0xcd, 0xcc, 0x4c, 0x3e, 0x0, 0x0, 0x80, 0x3f, 0x0, 0x0, 0x0, 0x0, 0xa, 0xd7, 0xa3, 0x3d, 0xcd, 0xcc, 0xcc, 0x3e, 0x0, 0x0, 0x0, 0x0, 0xa, 0xd7, 0x23, 0x3d, 0xcd, 0xcc, 0x4c, 0x3e, 0x0, 0x0, 0x0, 0x0, 0xa, 0xd7, 0xa3, 0x3c, 0xcd, 0xcc, 0xcc, 0x3d, 0x0, 0x0, 0x0, 0x0, 0xa, 0xd7, 0x23, 0x3c, 0xcd, 0xcc, 0x4c, 0x3d, 0x0, 0x0, 0x0, 0x0, 0x6f, 0x12, 0x83, 0x3b, 0xa, 0xd7, 0xa3, 0x3c, 0x0, 0x0, 0x0, 0x0, 0x6f, 0x12, 0x3, 0x3b, 0xa, 0xd7, 0x23, 0x3c, 0x0, 0x0, 0x0, 0x0, 0x17, 0xb7, 0x51, 0x3a, 0x6f, 0x12, 0x83, 0x3b, 0x0, 0x0, 0x0, 0x0, 0x17, 0xb7, 0xd1, 0x39, 0x6f, 0x12, 0x3, 0x3b, 0x0, 0x0, 0x0, 0x0, 0x17, 0xb7, 0x51, 0x39, 0x6f, 0x12, 0x83, 0x3a, 0x0, 0x0, 0x0, 0x0, 0xac, 0xc5, 0x27, 0x39, 0x17, 0xb7, 0x51, 0x3a, 0x0, 0x0, 0x0, 0x0, 0xac, 0xc5, 0xa7, 0x38, 0x17, 0xb7, 0xd1, 0x39, 0x0, 0x0, 0x0, 0x0, 0xac, 0xc5, 0x27, 0x38, 0x17, 0xb7, 0x51, 0x39, 0x0, 0x0, 0x0, 0x0, 0xac, 0xc5, 0xa7, 0x37, 0x17, 0xb7, 0xd1, 0x38, 0x0, 0x0, 0x0, 0x0, 0xac, 0xc5, 0x27, 0x37, 0x17, 0xb7, 0x51, 0x38, 0x0, 0x0, 0x0, 0x0, 0xbd, 0x37, 0x6, 0x37, 0xac, 0xc5, 0x27, 0x38, 0x0, 0x0, 0x0, 0x0, 0x30, 0x0, 0x0, 0x0, 0x0, 0x0, 0x80, 0x3f, 0x0, 0x0, 0x0, 0x40, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10, 0x0, 0x0, 0x0, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x2, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xd0, 0x12, 0x73, 0x41, 0x0, 0x0, 0x0, 0x0, 0x65, 0xcd, 0xcd, 0x41, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x65, 0xcd, 0xcd, 0x41, 0x0, 0x0, 0x0, 0x90, 0x1e, 0x6d, 0xe6, 0x41, 0xb, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x24, 0x40, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x34, 0x40, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3e, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x14, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x24, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2e, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x34, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x39, 0x40, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3e, 0x40, 0x2, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xd0, 0x12, 0x73, 0x41, 0x0, 0x0, 0x0, 0x90, 0x1e, 0x6d, 0xe6, 0x41, 0x10, 0x0, 0x0, 0x0, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4a, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b, 0x80, 0x96, 0x98, 0x4b)
            val output = handler.createReply()
            result.map { it.toByte() }.map(output::write_octet)
            return output
        }

        return super._invoke(method, _input, handler)
    }

    override fun connectPort() {
        TODO("Not yet implemented")
    }

    override fun releaseObject() {
        println("Released resource")
    }
}