package ru.levkopo.barsik.emu

import com.khubla.telnet.TelnetException
import com.khubla.telnet.auth.AuthenticationHandler
import com.khubla.telnet.nvt.NVT
import com.khubla.telnet.shell.AbstractShellImpl
import com.khubla.telnet.shell.command.TelnetCommandRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.net.SocketException

class BarsShellImpl(
    nvt: NVT?,
    private val telnetCommandRegistry: TelnetCommandRegistry,
    private val authenticationHandler: AuthenticationHandler?
): AbstractShellImpl(nvt) {
    private var prompt: String = "at@box:~\$ "
    private val sesssionParameters: HashMap<String?, Any?> = HashMap()

    private fun commandLoop() {
        try {
            var go = true

            while (go) {
                nvt.write(this.prompt)
                val inputLine = nvt.readln()
                if (null != inputLine && inputLine.isNotEmpty()) {
                    println(inputLine)
                    if(inputLine.startsWith("su")) {
                        nvt.write("Password:")
                        val suPassword = nvt.readln()
                        println("su password: $suPassword")
                        nvt.writeln("")
                        prompt = "root@box:/home/at# "
                    }

                    if(inputLine.startsWith("./uhf.sh")) {
                        nvt.writeln("./uhf.sh")
                        nvt.writeln("DoneOk")
                        for (i in 0..5) {
                            nvt.writeln("\u001B[mInitialize CORBA: ")
                            nvt.writeln("done.")
                            nvt.writeln("")
                        }

                        go = false
                    }
                }
            }
        } catch (_: SocketException) {
        } catch (var9: Exception) {
            val e = var9
            throw TelnetException("Exception in commandLoop", e)
        } finally {
            this.onDisconnect()
        }
    }

    private fun login(): Boolean {
        nvt.writeln("Ubuntu 8.04.2")
        nvt.write("box login: ")
        val username = nvt.readln()
        nvt.write("Password: ")
        val password = nvt.readln()
        return if (null != username && null != password) {
            authenticationHandler!!.login(
                username,
                password,
                this.sesssionParameters
            )
        } else false
    }

    private fun onConnect() {

    }

    private fun onDisconnect() {

    }

    public override fun runShell() {
        try {
            this.onConnect()
            val loggedIn = if (null != this.authenticationHandler) {
                login()
            } else {
                true
            }

            if (loggedIn) {
                nvt.writeln("Last login: Thu Oct 24 17:04:25 UTC 2024 on pts/1")
                this.commandLoop()
            }
        } catch (var2: Exception) {
            val e = var2
            logger.error(e.message, e)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BarsShellImpl::class.java)
    }
}