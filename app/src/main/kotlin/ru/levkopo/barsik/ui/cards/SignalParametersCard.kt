package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.repositories.SignalRepository

@Composable
fun SignalParametersCard() {
    var frequency by remember { mutableStateOf(SignalConfig.frequency / 1000000) }
    var width by remember { mutableStateOf(SignalConfig.width / 1000) }
    var filter by remember { mutableStateOf(SignalConfig.filter) }
    var attenuator by remember { mutableStateOf(SignalConfig.attenuator) }
    var channel by remember { mutableStateOf(SignalConfig.channel) }
    val isInitialized by SignalRepository.isInitialized.collectAsState()
    val isRunning by SignalRepository.isRunning.collectAsState()

    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        modifier = Modifier
            .width(420.dp)
    ) {
        Column {
            Text(
                text = "Предустановки",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(30.dp),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = frequency.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            frequency = it.toDouble()
                            SignalConfig.frequency = it.toDouble() * 1000000
                        },
                        label = { Text("Частота, МГц") },
                        modifier = Modifier
                            .width(210.dp)
                    )
                    OutlinedTextField(
                        value = width.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            width = it.toDouble()
                            SignalConfig.width = it.toDouble() * 1000
                        },
                        label = { Text("Ширина канала, кГц") },
                        modifier = Modifier
                            .width(210.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = filter.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            filter = it.toFloat()
                            SignalConfig.filter = it.toFloat()
                        },
                        label = { Text("Фильтр") },
                        modifier = Modifier
                            .width(165.dp)
                    )
                    OutlinedTextField(
                        value = attenuator.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            attenuator = it.toInt().toShort()
                            SignalConfig.attenuator = it.toInt().toShort()
                        },
                        label = { Text("Аттенюатор") },
                        modifier = Modifier
                            .width(127.dp)
                    )
                    OutlinedTextField(
                        value = channel.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            channel = it.toInt()
                            SignalConfig.channel = it.toInt()
                        },
                        label = { Text("Канал") },
                        modifier = Modifier
                            .width(127.dp)
                    )
                }
            }

            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Spacer(Modifier.weight(4f))
                OutlinedIconButton(
                    enabled = isInitialized,
                    onClick = {
                        if (isRunning) {
                            SignalRepository.stopSignalDataExchange()
                        } else {
                            SignalRepository.startSignalDataExchange()
                        }
                    }
                ) {
                    Icon(
                        when {
                            isRunning -> Icons.Filled.Close
                            else -> Icons.Filled.PlayArrow
                        }, contentDescription = null
                    )
                }
            }
        }
    }
}