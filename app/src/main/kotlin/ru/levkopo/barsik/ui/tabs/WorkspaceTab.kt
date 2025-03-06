package ru.levkopo.barsik.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.ui.cards.SignalParametersCard

val series = XYSeries("Sample Data")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WorkspaceTab() {
    val fftResult by SignalRepository.fftResult.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column {
                SignalParametersCard()
            }

            Column {
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
                        factory = {
                            val series = XYSeries("Sample Data")
                            series.add(1.0, 1.0)

                            val dataset = XYSeriesCollection(series)
                            val chart: JFreeChart = ChartFactory.createXYLineChart(
                                "Line Chart", // Title
                                "X-Axis", // X-Axis Label
                                "Y-Axis", // Y-Axis Label
                                dataset // Dataset
                            )

                            // Create a ChartPanel and add it to the JPanel
                            ChartPanel(chart)
                        },
                        update = {
//                            series.clear()
                            fftResult.forEach {
                                series.add(it.frequency, it.amplitude)
                            }

                            series.fireSeriesChanged()
                        }
                    )
//                    CartesianChartHost(
//                        chart =
//                            rememberCartesianChart(
//                                rememberLineCartesianLayer(),
//                                startAxis = VerticalAxis.rememberStart(),
//                                bottomAxis = HorizontalAxis.rememberBottom(),
//                            ),
//                        model = CartesianChartModel(
//                            LineCartesianLayerModel.build {
//                                if (fftResult.isEmpty()) {
//                                    series(0, 0, 0, 0)
//                                } else {
//                                    series(
//                                        x = fftResult.map { it.frequency },
//                                        y = fftResult.map { it.amplitude },
//                                    )
//                                }
//                            }
//                        ),
//                        modifier = Modifier.fillMaxSize(),
//                    )
                }
            }
        }
    }
}