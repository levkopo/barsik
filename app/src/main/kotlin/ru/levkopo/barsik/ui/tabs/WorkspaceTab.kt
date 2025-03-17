package ru.levkopo.barsik.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.levkopo.barsik.ui.cards.SignalGraphCard
import ru.levkopo.barsik.ui.cards.SignalCalculationParametersCard
import ru.levkopo.barsik.ui.cards.SignalTableCard
import ru.levkopo.barsik.ui.cards.SignalParametersCard

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
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                SignalParametersCard()
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                SignalGraphCard()
                Row(
                    modifier = Modifier.width(1200.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    SignalCalculationParametersCard()
                    SignalTableCard()
                }
            }
        }
    }
}