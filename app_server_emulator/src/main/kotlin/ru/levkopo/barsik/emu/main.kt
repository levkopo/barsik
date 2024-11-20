package ru.levkopo.barsik.emu

import DSP.SigBoardInfo3
import DSP.SigBoardInfo3Helper
import com.khubla.telnet.TelnetServer
import com.khubla.telnet.nvt.NVT
import com.khubla.telnet.nvt.spy.NVTSpy
import com.khubla.telnet.nvt.spy.impl.ConsoleNVTSpyImpl
import com.khubla.telnet.shell.Shell
import com.khubla.telnet.shell.ShellFactory
import com.khubla.telnet.shell.basic.BasicTelnetCommandRegistryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.jacorb.orb.CDRInputStream
import org.omg.CORBA.portable.BoxedValueHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder


private val mainScope = CoroutineScope(Dispatchers.Default)
private val orbdProcessFlow = MutableStateFlow<Process?>(null)
private val runtime = Runtime.getRuntime()

@OptIn(ExperimentalStdlibApi::class)
fun main() {
//    thread {
//        IRBrowser.main(arrayOf(
//            "-i", "corbaloc::$LINUX_BOX_IP:$LINUX_BOX_ORB_PORT/NameService"
//        ))
//    }

//    return
//    val orb = org.jacorb.orb.ORB.init(arrayOf(), Config.orbProperties)
//    val sig = SigBoardInfo3(
//        arrayOf(
//            MapEntry("IdRcv", 1),
//            MapEntry("IdCon", 1),
////            MapEntry("IdSwt", 12),
////            MapEntry("Filters", 1),
////            MapEntry("SampleFreqs", 1),
////            MapEntry("RangeBands", 1),
////            MapEntry("WatchTimes", 1),
////            MapEntry("WinParams", 1),
////            MapEntry("SuperRangeBands", 1),
//        )
//    )
//
//    val poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"))
//    poa.the_POAManager().activate()
//
//
////    poa.activate_object(sig)
////
////    val obj = poa.servant_to_reference(sig)
//
//    val out = CDROutputStream()
//    SigBoardInfo3Helper.write(out, sig)
//    out.create_input_stream()
//    println(out.bufferCopy.toHexString())
//    println(out.bufferCopy.decodeToString())



    val inp = CDRInputStream(
        "ac220bca737800c03a9db56608004500016c76e4400040067f5d0a4217640a421763bb547d001e0004a00ef0cdec8018002efa3900000101080a000f535a0000c0e9b042923cbff15b3defe1e43c72a40fbdaccf95bd3e4b55bec25a0dbdc6c33cbef25a123e842a853d083f063a966fcf3efe67a93d32a2613ead51d3bdb55664bd47715cbec5ce5d3b30ab13be1fa2273e9d3bba3c62a4d93d6558b03b265eaa3c531abd3df84c353df7c0263e82e320be677fed3cb9973f3ed24ae13d6db2483d141f0f3e6b3d103da07131bd79d46c3ec8d8ed3d6aa7153e6ae30f3ef72d2cbdfd09523e3fc5c3bd708600bd310d5cbe59b323bea0c9e7beb9e455bedde4a0be2d8d59bdf6dc4abe8981b93cf74a4fbe2a6e9cbc04c2593d2de7d43dd6b90cbe0206ebbbdd083d3e591305be20e907bd3a571bbd488ac33dfb4830bd21c7893e7951123d5b73e9bd1a17fb3d240351bec1c7e33d37dfa93d1e77c7bcad47ae3ec7cf8d3c0000000000000000010000000100000000000000"
        .chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
    )
    inp.setLittleEndian(true)

//    val doubleArray = DoubleArray(10000)
//    inp.read_double_array(doubleArray, 0, 10)
//    println(doubleArray.joinToString(","))
//
    println(inp.read_double())
    println(inp.read_double())
    println(inp.read_double())
//    println(inp.read_long_array(IntArray(8), 0, 8))
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_long())
//    println(inp.read_boolean())
//    println(inp.read_boolean())
//    println(inp.read_long())
//    println(inp.read_boolean())
//    println(inp.read_boolean())


    launchORBObserver()

    TelnetServer(23, 20, object : ShellFactory {
        override fun createShell(nvt: NVT): Shell = BarsShellImpl(
            nvt,
            BasicTelnetCommandRegistryImpl()
        ) { login, password, _ ->
            println("login: $login, password: $password")
            true
        }

        override fun getNVTSpy(): NVTSpy = ConsoleNVTSpyImpl()
    }).run()
}