package ru.levkopo.barsik.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import ru.levkopo.barsik.ui.cards.SignalLogsCard
import ru.levkopo.barsik.ui.cards.SignalGraphCard
import ru.levkopo.barsik.ui.cards.SignalCalculationParametersCard
import ru.levkopo.barsik.ui.cards.SignalTableCard
import ru.levkopo.barsik.ui.cards.SignalParametersCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WorkspaceTab(windowState: WindowState) {
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
                SignalLogsCard(Modifier.height(420.dp))
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                SignalGraphCard(
                    windowState,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.width(1200.dp).padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    SignalCalculationParametersCard()
                    SignalTableCard()
                }
            }
        }
    }
}