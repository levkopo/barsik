package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.data.repositories.SystemBoardInformationRepository
import ru.levkopo.barsik.ui.SignalSettings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SignalCalculationParametersCard() {
    val selectedScale by SignalSettings.graphScale.collectAsState()
    val detectorAmplitude by SignalSettings.detectorAmplitude.collectAsState()
    val systemBoardInfo by SystemBoardInformationRepository.systemBoardInfo.collectAsState()

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
                text = "Настройки измерений",
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Шкала измерений : ")

                    Spacer(Modifier.weight(1f))

                    SingleChoiceSegmentedButtonRow {
                        SignalSettings.Scale.entries.forEachIndexed { index, scale ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = SignalSettings.Scale.entries.size
                                ),
                                onClick = {
                                    SignalSettings.graphScale.value = scale
                                },
                                selected = scale == selectedScale,
                                label = { Text(scale.title) }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = detectorAmplitude.toString(),
                onValueChange = {
                    SignalSettings.detectorAmplitude.value = it.toDoubleOrNull() ?: 1000.0
                    SignalRepository.clearTable()
                },
                label = { Text("Минимальная амплитуда детектора") },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text(selectedScale.title) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = VisualTransformation.None,
            )

            OutlinedButton(onClick = {
                SignalRepository.clearTable()
            }) {
                Text("Очистить таблицу")
            }
        }
    }
}