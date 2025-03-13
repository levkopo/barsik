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
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import ru.levkopo.barsik.data.repositories.SignalRepository
import java.awt.Color

val series = XYSeries("Частота")
@Composable
fun SignalGraphCard() {
    val fftResult by SignalRepository.fftResult.collectAsState()

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
                val dataset = XYSeriesCollection(series)
                val chart: JFreeChart = ChartFactory.createXYLineChart(
                    "",
                    "Частота, МГц",
                    "Амплитуда",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    false,
                    false
                )


                val xyPlot = chart.plot as XYPlot
                xyPlot.renderer.defaultPaint = Color.getColor("#65558F")

                ChartPanel(chart).apply {
                    background = null
                }
            },
            update = {
                series.clear()
                fftResult.forEach {
                    series.add(it.frequency, it.amplitude / 1000)
                }

                series.fireSeriesChanged()
            }
        )
//        CartesianChartHost(
//            chart =
//                rememberCartesianChart(
//                    rememberLineCartesianLayer(),
//                    startAxis = VerticalAxis.rememberStart(),
//                    bottomAxis = HorizontalAxis.rememberBottom(),
//                ),
//            model = CartesianChartModel(
//                LineCartesianLayerModel.build {
//                    if (fftResult.isEmpty()) {
//                        series(0, 0, 0, 0)
//                    } else {
//                        series(
//                            x = fftResult.map { it.frequency },
//                            y = fftResult.map { it.amplitude },
//                        )
//                    }
//                }
//            ),
//            modifier = Modifier.fillMaxSize(),
//        )
    }
}