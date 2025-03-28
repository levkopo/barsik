package ru.levkopo.barsik.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.levkopo.barsik.data.repositories.LogsRepository.Log.Type

object LogsRepository {
    data class Log(
        val type: Type,
        val title: String,
        val message: String,
    ) {
        enum class Type {
            INFO, ERROR, WARNING
        }
    }

    private var _logs: MutableStateFlow<List<Log>> = MutableStateFlow(emptyList())
    val logs = _logs.asStateFlow()

    fun error(
        title: String,
        message: String,
    ) {
        log(Type.ERROR, title, message)
    }

    fun error(
        title: String,
        error: Throwable,
    ) {
        log(Type.ERROR, title, error.stackTraceToString())
    }

    fun warning(
        title: String,
        message: String,
    ) {
        log(Type.WARNING, title, message)
    }

    fun info(
        title: String,
        message: String,
    ) {
        log(Type.INFO, title, message)
    }

    fun log(
        type: Type,
        title: String,
        message: String,
    ) {
        when (type) {
            Type.INFO -> {
                println("INFO $title: $message")
            }
            Type.WARNING -> {
                println("WARN $title: $message")
            }
            Type.ERROR -> {
                System.err.println("ERROR $title: $message")
            }
        }
        _logs.value = ArrayList<Log>(_logs.value).apply {
            add(Log(type, title, message))
        }
    }
}