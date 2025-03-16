package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.AbstractRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.ui.SignalSettings
import java.awt.BasicStroke
import java.awt.Color

val spectrumSeries = XYSeries("Спектр сигнала")
val detectorSeries = XYSeries("Минимальная амплитуда детектора")

@Composable
fun SignalGraphCard() {
    val detectorAmplitude by SignalSettings.detectorAmplitude.collectAsState()
    val selectedScale by SignalSettings.graphScale.collectAsState()
    val fftResult by SignalRepository.currentSpectrum.collectAsState()

    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        modifier = Modifier
            .width(720.dp)
            .height(420.dp)
    ) {
        SwingPanel(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val dataset = XYSeriesCollection()
                dataset.addSeries(detectorSeries)
                dataset.addSeries(spectrumSeries)

                val chart: JFreeChart = ChartFactory.createXYLineChart(
                    "",
                    "Частота, МГц",
                    "",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    false,
                    false
                )

                val xyPlot = chart.plot as XYPlot
                val renderer = xyPlot.renderer as AbstractRenderer
                renderer.defaultStroke = BasicStroke(1.5f)
                renderer.defaultPaint = Color.BLACK
                renderer.autoPopulateSeriesStroke = false

                ChartPanel(chart).apply {
                    background = null
                }
            },
            update = { panel ->
                val xyPlot = panel.chart.plot as XYPlot
                xyPlot.rangeAxis.label = "Амплитуда, " + when (selectedScale) {
                    SignalSettings.Scale.DBM  -> "дБм"
                    SignalSettings.Scale.MICRO_VOLT  -> "мкВ"
                }

                detectorSeries.clear()
                fftResult.forEach {
                    detectorSeries.add(it.frequency, detectorAmplitude)
                }

                detectorSeries.fireSeriesChanged()

                spectrumSeries.clear()
                fftResult.forEach {
                    spectrumSeries.add(it.frequency, when (selectedScale) {
                        SignalSettings.Scale.DBM  -> it.dBm
                        SignalSettings.Scale.MICRO_VOLT  -> it.voltage
                    })
                }

                SignalSettings.maxAmplitude = SignalSettings.maxAmplitude.coerceAtLeast(spectrumSeries.maxY).coerceAtLeast(detectorSeries.maxY)
                SignalSettings.minAmplitude = SignalSettings.minAmplitude.coerceAtMost(spectrumSeries.minY).coerceAtMost(detectorSeries.minY)
                xyPlot.rangeAxis.setRange(SignalSettings.minAmplitude, SignalSettings.maxAmplitude + 1)
                spectrumSeries.fireSeriesChanged()
            }
        )
    }
}