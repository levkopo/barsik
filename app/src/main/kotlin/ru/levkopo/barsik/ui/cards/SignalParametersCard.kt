package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.remote.SignalOrbManager
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.data.repositories.SystemBoardInformationRepository
import ru.levkopo.barsik.models.asString
import kotlin.text.String

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SignalParametersCard() {
    val systemBoardInfo by SystemBoardInformationRepository.systemBoardInfo.collectAsState()
    var frequency by remember { mutableStateOf(SignalConfig.frequency / 1000000) }
    var width by remember { mutableStateOf(SignalConfig.width / 1000) }
    var filter by remember { mutableStateOf(SignalConfig.filter / 1000) }
    var attenuator by remember { mutableStateOf(SignalConfig.attenuator) }
    var channel by remember { mutableStateOf(SignalConfig.channel) }
    var filterExpanded by remember { mutableStateOf(false) }
    var attenuatorExpanded by remember { mutableStateOf(false) }
    var channelExpanded by remember { mutableStateOf(false) }
    var serverStarting by remember { mutableStateOf(false) }
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
                            frequency = it.toDoubleOrNull() ?: (SignalConfig.frequency / 1000000)
                            SignalConfig.frequency = frequency * 1000000
                        },
                        label = { Text("Частота") },
                        modifier = Modifier
                            .width(210.dp),
                        suffix = { Text("МГц") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        visualTransformation = VisualTransformation.None
                    )
                    OutlinedTextField(
                        value = width.toString(),
                        enabled = isInitialized,
                        onValueChange = {
                            width = it.toDoubleOrNull() ?: (SignalConfig.width / 1000)
                            SignalConfig.width = width * 1000
                        },
                        label = { Text("Полоса") },
                        modifier = Modifier
                            .width(210.dp),
                        suffix = { Text("кГц") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        visualTransformation = VisualTransformation.None
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = filterExpanded,
                        onExpandedChange = { filterExpanded = !filterExpanded },
                        modifier = Modifier
                            .width(165.dp)
                    ) {
                        OutlinedTextField(
                            value = String.format("%.3f", filter).replace(',', '.'),
                            enabled = isInitialized,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = filterExpanded,
                                    modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable).onClick {
                                        filterExpanded = !filterExpanded
                                    },
                                )
                            },
                            suffix = { Text("кГц") },
                            label = { Text("Фильтр") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable),
                        )

                        ExposedDropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = {
                                filterExpanded = false
                            }
                        ) {
                            systemBoardInfo?.filters?.forEach {
                                DropdownMenuItem(
                                    text = { Text(String.format("%.3f", it / 1000).replace(',', '.') + " кГц") },
                                    onClick = {
                                        SignalConfig.filter = it
                                        filter = it / 1000
                                        filterExpanded = false
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text(String.format("%.3f", 1000000f / 1000).replace(',', '.') + " кГц") },
                                onClick = {
                                    SignalConfig.filter = 1000000f
                                    filter = 1000f
                                    filterExpanded = false
                                }
                            )
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = attenuatorExpanded,
                        onExpandedChange = { attenuatorExpanded = !attenuatorExpanded },
                        modifier = Modifier
                            .width(127.dp)
                    ) {
                        OutlinedTextField(
                            value = attenuator.toString(),
                            enabled = isInitialized,
                            onValueChange = {},
                            label = { Text("Аттенюатор") },
                            suffix = { Text("дБ") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = attenuatorExpanded,
                                    modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable).onClick {
                                        attenuatorExpanded = !attenuatorExpanded
                                    },
                                )
                            },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable)
                        )

                        ExposedDropdownMenu(
                            expanded = attenuatorExpanded,
                            onDismissRequest = {
                                attenuatorExpanded = false
                            }
                        ) {
                            listOf<Short>(0, 24).forEach {
                                DropdownMenuItem(
                                    text = { Text("$it дБ") },
                                    onClick = {
                                        SignalConfig.attenuator = it
                                        attenuator = it
                                        attenuatorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = channelExpanded,
                        onExpandedChange = { channelExpanded = !channelExpanded },
                        modifier = Modifier
                            .width(127.dp)
                    ) {
                        OutlinedTextField(
                            value = channel.toString(),
                            enabled = isInitialized,
                            onValueChange = {
                                channel = it.toInt()
                                SignalConfig.channel = it.toInt()
                            },
                            label = { Text("Канал") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = channelExpanded,
                                    modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable).onClick {
                                        channelExpanded = !channelExpanded
                                    },
                                )
                            },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable)
                        )

                        ExposedDropdownMenu(
                            expanded = channelExpanded,
                            onDismissRequest = {
                                channelExpanded = false
                            }
                        ) {
                            systemBoardInfo?.attenuator?.bands?.forEach {
                                DropdownMenuItem(
                                    text = { Text((it.id + 1).toString()) },
                                    onClick = {
                                        SignalConfig.channel = (it.id + 1)
                                        channel = it.id + 1
                                        channelExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Spacer(Modifier.weight(4f))
                when {
                    isInitialized -> OutlinedIconButton(
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

                    else -> {
                        Button(
                            enabled = !serverStarting,
                            onClick = {
                                serverStarting = true
                                CoroutineScope(Dispatchers.Default).launch {
                                    serverStarting = SignalOrbManager.start()
                                }
                            }
                        ) {
                            Text(
                                text = "Запустить сервер"
                            )
                        }
                    }
                }
            }
        }
    }
}