package ru.levkopo.barsik.emu

import com.khubla.telnet.TelnetServer
import com.khubla.telnet.nvt.NVT
import com.khubla.telnet.nvt.spy.NVTSpy
import com.khubla.telnet.nvt.spy.impl.LoggingNVTSpyImpl
import com.khubla.telnet.shell.Shell
import com.khubla.telnet.shell.ShellFactory
import com.khubla.telnet.shell.basic.BasicTelnetCommandRegistryImpl

fun main() {
    TelnetServer(2000, 20, object : ShellFactory {
        override fun createShell(nvt: NVT): Shell = BarsShellImpl(
            nvt,
            BasicTelnetCommandRegistryImpl()
        ) { login, password, _ ->
            println("login: $login, password: $password")
            true
        }

        override fun getNVTSpy(): NVTSpy = LoggingNVTSpyImpl()
    }).run()
}