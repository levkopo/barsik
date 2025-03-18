package ru.levkopo.barsik.data

interface IqDataSource {
    fun getNextIqSamples(count: Int): List<Pair<Double, Double>>
}