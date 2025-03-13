package ru.levkopo.barsik.emu.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import ru.levkopo.barsik.emu.modulators

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulatorsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val modulator = modulators[selectedTab]
    val isRunning by modulator.isRunning.collectAsState(initial = false)

    Column {
        SecondaryTabRow(selectedTab) {
            modulators.forEachIndexed { index, modulator ->
                Tab(
                    selected = index == selectedTab,
                    onClick = { selectedTab = index }
                ) {
                    Text(modulator.javaClass.simpleName)
                }
            }
        }

        Row {
            Text("Запустить модулятор")

            Switch(
                checked = isRunning,
                onCheckedChange = {
                    if(it) {
                        modulator.start()
                    }else modulator.stop()
                }
            )
        }

        modulator.SettingsScreen()
    }
}