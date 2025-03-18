package ru.levkopo.barsik.configs

/**
 * Конфигурация требования сигналов
 */
object SignalConfig: BaseConfig() {
    /**
     * Частота
     */
    var frequency by doubleProperty(3.43E8)

    /**
     * Аттенюатор
     */
    var attenuator by shortProperty(24)

    /**
     * Неизвестно, но надо
     */
    var c by doubleProperty(1800000.0)

    /**
     * Неизвестно, но надо
     */
    var d by doubleProperty(1.2600225506490411E-257)

    /**
     * Неизвестно, но надо
     */
    var f by doubleProperty(4.195177201736488E-308)

    /**
     * Ширина спектра
     */
    var width by doubleProperty(1200000.0)

    /**
     * Используемый фильтр
     */
    var filter by floatProperty(2500.0f)

    /**
     * Качество фазы (?)
     */
    var qualityPhase by floatProperty(0.004f)

    /**
     * Неизвестно, но надо
     */
    var ae by intProperty(0)

    /**
     * Используемый канал
     */
    var channel by intProperty(1)
}