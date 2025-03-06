package ru.levkopo.barsik.ui.tabs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.configs.SignalConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun WorkspaceTab() {
    var frequency by remember { mutableStateOf(SignalConfig.frequency / 1000000) }
    var width by remember { mutableStateOf(SignalConfig.width / 1000) }

    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        modifier = Modifier
            .width(220.dp)
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
            Row {
                OutlinedTextField(
                    value = frequency.toString(),
                    onValueChange = {
                        frequency = it.toDouble()
                        SignalConfig.frequency = it.toDouble() * 1000000
                    },
                    label = { Text("Частота, МГц") },
                )
                OutlinedTextField(
                    value = width.toString(),
                    onValueChange = {
                        width = it.toDouble()
                        SignalConfig.width = it.toDouble() * 1000
                    },
                    label = { Text("Ширина канала, кГц") },
                )
            }
        }
    }
}