package ru.levkopo.barsik.configs

import kotlin.reflect.KProperty

abstract class BaseConfig {
    protected fun stringProperty(defaultValue: String) = PropertyDelegate(defaultValue)
    protected fun doubleProperty(defaultValue: Double) = PropertyDelegate(defaultValue)
    protected fun intProperty(defaultValue: Int) = PropertyDelegate(defaultValue)
    protected fun floatProperty(defaultValue: Float) = PropertyDelegate(defaultValue)
    protected fun shortProperty(defaultValue: Short) = PropertyDelegate(defaultValue)

    protected class PropertyDelegate<T>(private val defaultValue: T) {
        private var currentValue: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            var currentValue = currentValue
            if(currentValue == null) {
                return defaultValue
            }

            return currentValue
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            currentValue = value
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }
}