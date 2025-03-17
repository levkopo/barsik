package ru.levkopo.barsik.data.remote

import kotlinx.coroutines.delay
import org.apache.commons.net.telnet.TelnetClient
import ru.levkopo.barsik.configs.ApplicationConfig
import ru.levkopo.barsik.configs.ServerConfig
import java.io.PrintStream

internal class TelnetInitializer {
    private val client = TelnetClient().apply {
        connectTimeout = 5000
    }

    suspend fun initialize() = runCatching {
        client.connect(ServerConfig.linuxBoxIp)
        delay(100)

        val outputStream = PrintStream(client.outputStream)
        outputStream.println(ServerConfig.linuxBoxUsername)
        outputStream.flush()
        delay(100)

        outputStream.println(ServerConfig.linuxBoxPassword)
        outputStream.flush()
        delay(100)

        outputStream.println("su\r\n")
        outputStream.flush()
        delay(100)

        outputStream.println(ServerConfig.linuxBoxSUPassword)
        outputStream.flush()
        delay(100)

        outputStream.println(ApplicationConfig.serverStartupCommandName)
        outputStream.flush()

        delay(1000)

        client.disconnect()

        delay(5000)
    }
}