package ru.levkopo.barsik.data.remote

import kotlinx.coroutines.delay
import org.apache.commons.net.telnet.TelnetClient
import ru.levkopo.barsik.configs.ApplicationConfig
import ru.levkopo.barsik.configs.ServerConfig
import ru.levkopo.barsik.data.repositories.LogsRepository
import java.io.PrintStream

/**
 * Логика инициализации сервера по telnet
 */
internal class TelnetInitializer {
    private val client = TelnetClient().apply {
        connectTimeout = 5000
    }

    suspend fun initialize(): Boolean {
        LogsRepository.info(javaClass.simpleName, "Инициализация Telnet")
        try {
            LogsRepository.info(javaClass.simpleName, "Подключение к серверу Telnet...")
            client.connect(ServerConfig.linuxBoxIp)
            delay(100)

            LogsRepository.info(javaClass.simpleName, "Успешное подключение")
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

            LogsRepository.info(javaClass.simpleName, "Успешная авторизация")
            LogsRepository.info(javaClass.simpleName, "Запуск сервера")
            outputStream.println(ApplicationConfig.serverStartupCommandName)
            outputStream.flush()

            delay(1000)

            LogsRepository.info(javaClass.simpleName, "Отключение от Telnet сервера")
            client.disconnect()

            delay(5000)

            LogsRepository.info(javaClass.simpleName, "Успешная инициализация")
            return true
        }catch (e: Throwable) {
            LogsRepository.error(javaClass.simpleName, e)
            return false
        }
    }
}