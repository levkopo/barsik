package ru.levkopo.barsik.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jfree.data.xy.XYSeries
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.ui.cards.SignalGraphCard
import ru.levkopo.barsik.ui.cards.SignalTableCard
import ru.levkopo.barsik.ui.cards.SignalParametersCard

val series = XYSeries("Sample Data")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WorkspaceTab() {
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

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                SignalGraphCard()
                SignalTableCard()
            }
        }
    }
}