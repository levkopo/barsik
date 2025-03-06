package ru.levkopo.barsik.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import ru.levkopo.barsik.ui.tabs.ExportTab
import ru.levkopo.barsik.ui.tabs.SettingsTab
import ru.levkopo.barsik.ui.tabs.WorkspaceTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppScreen() {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Рабочая зона", "Экспорт", "Настройки")
    Column {
        SecondaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        when (state) {
            0 -> WorkspaceTab()
            1 -> ExportTab()
            2 -> SettingsTab()
        }
    }
}