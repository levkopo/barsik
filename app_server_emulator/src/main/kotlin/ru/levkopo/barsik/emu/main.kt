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

/**
 * Путь до JDK 1.8 (liberica-full)
 * Обязательный для запуска
 */
const val JVM18_PATH = "/home/levkopo/.jdks/liberica-full-1.8.0_442"

/**
 * Запуск эмулятора
 */
@OptIn(ExperimentalStdlibApi::class)
fun main() {
    // Запуск orb сервера
    launchORBObserver()

    // Запуск telnet сервера
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

    // Запуск UI для управления эмулятором
    application {
        Window(onCloseRequest = ::exitApplication) {
            AppScreen()
        }
    }
}