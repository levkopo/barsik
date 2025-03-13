package ru.levkopo.barsik.emu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.khubla.telnet.TelnetServer
import com.khubla.telnet.nvt.NVT
import com.khubla.telnet.nvt.spy.NVTSpy
import com.khubla.telnet.nvt.spy.impl.ConsoleNVTSpyImpl
import com.khubla.telnet.shell.Shell
import com.khubla.telnet.shell.ShellFactory
import ru.levkopo.barsik.emu.ui.AppScreen
import kotlin.concurrent.thread

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    launchORBObserver()
    thread {
        TelnetServer(23, 20, object : ShellFactory {
            override fun createShell(nvt: NVT): Shell = BarsShellImpl(
                nvt
            ) { login, password, _ ->
                println("login: $login, password: $password")
                true
            }

            override fun getNVTSpy(): NVTSpy = ConsoleNVTSpyImpl()
        }).run()
    }

    application {
        Window(onCloseRequest = ::exitApplication) {
            AppScreen()
        }
    }
}