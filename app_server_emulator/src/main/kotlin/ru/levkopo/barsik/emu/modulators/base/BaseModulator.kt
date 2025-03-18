package ru.levkopo.barsik.emu.modulators.base

import DSP.iq
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.levkopo.barsik.emu.modulators.base.ModulatorsManager

/**
 * Абстрактный класс модулятора сигналов
 */
abstract class BaseModulator {
    /**
     * Переменная, хранящая смодулированые iq-сигналы
     */
    internal var currentIQ: Array<iq>
        get() = ModulatorsManager.currentIQ
        set(value) {
            ModulatorsManager.currentIQ = value
        }

    /**
     * Переменная, хранящая текущую используемую частоту
     */
    internal val carrierFrequency: Double
        get() = ModulatorsManager.carrierFrequency

    /**
     * Переменная, хранящая функции для работы над текущей задачей
     */
    private var job: Job? = null

    /**
     * Переменная, хранящая запущена ли генерация iq сигналов
     */
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    /**
     * Функция, запускающаяся для генерации iq сигналов
     */
    internal abstract suspend fun run()

    /**
     * Экран настроек модулятора
     */
    @Composable
    abstract fun SettingsScreen()

    /**
     * Запуск модулятора
     */
    fun start() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            _isRunning.value = true

            runCatching {
                run()
            }.onFailure {
                it.printStackTrace()
            }

            _isRunning.value = false
        }
    }

    /**
     * Остановка модулятора
     */
    fun stop() {
        job?.cancel()
        _isRunning.value = false
    }
}