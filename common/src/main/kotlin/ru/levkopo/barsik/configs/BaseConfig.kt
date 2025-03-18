package ru.levkopo.barsik.configs

import java.io.File
import java.util.prefs.Preferences
import kotlin.reflect.KProperty

/**
 * Шаблонный класс предназначенный для создания конфигураций
 */
abstract class BaseConfig {
    /**
     * Утилита для сохранения настроек в папке пользователя
     */
    private val preferences = Preferences.userRoot().node(this.javaClass.simpleName)

    /**
     * Переменная конфигурации с типом строки
     */
    protected fun stringProperty(defaultValue: String) = PropertyDelegate(
        defaultValue = defaultValue,
        resolveSavedValue = { preferences.get(it, defaultValue) },
        save = { name, value ->  preferences.put(name, value) }
    )

    /**
     * Переменная конфигурации с типом плавающей запятой (64 бита)
     */
    protected fun doubleProperty(defaultValue: Double) = PropertyDelegate(
        defaultValue = defaultValue,
        resolveSavedValue = { preferences.getDouble(it, defaultValue) },
        save = { name, value ->  preferences.putDouble(name, value) }
    )

    /**
     * Переменная конфигурации с типом целочисленным значением
     */
    protected fun intProperty(defaultValue: Int) = PropertyDelegate(
        defaultValue = defaultValue,
        resolveSavedValue = { preferences.getInt(it, defaultValue) },
        save = { name, value ->  preferences.putInt(name, value) }
    )

    /**
     * Переменная конфигурации с типом плавающей запятой (32 бита)
     */
    protected fun floatProperty(defaultValue: Float) = PropertyDelegate(
        defaultValue = defaultValue,
        resolveSavedValue = { preferences.getFloat(it, defaultValue) },
        save = { name, value ->  preferences.putFloat(name, value) }
    )

    /**
     * Переменная конфигурации с типом целочисленным значением (16 бит)
     */
    protected fun shortProperty(defaultValue: Short) = PropertyDelegate(
        defaultValue = defaultValue,
        resolveSavedValue = { preferences.getInt(it, defaultValue.toInt()).toShort() },
        save = { name, value ->  preferences.putInt(name, value.toInt()) }
    )

    /**
     * Шаблонный класс предназначенный для создания конфигураций
     */
    protected class PropertyDelegate<T>(
        private val defaultValue: T,
        private val resolveSavedValue: (name: String) -> T?,
        private val save: (name: String, T) -> Unit
    ) {
        private var currentValue: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            var currentValue = currentValue
            if (currentValue == null) {
                currentValue = resolveSavedValue(property.name) ?: defaultValue
            }

            return currentValue!!
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            currentValue = value
            save(property.name, value)
        }
    }
}