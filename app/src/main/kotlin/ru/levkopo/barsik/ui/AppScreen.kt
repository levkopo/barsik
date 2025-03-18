package ru.levkopo.barsik.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import io.github.evanrupert.excelkt.workbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import ru.levkopo.barsik.data.repositories.SignalRepository
import ru.levkopo.barsik.ui.tabs.SettingsTab
import ru.levkopo.barsik.ui.tabs.WorkspaceTab
import java.io.File
import java.util.*
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

val fileChooser = JFileChooser().apply {
    val date = Date()
    setSelectedFile(File("Barsic Export $date.xlsx".replace(":", ".")))
    resetChoosableFileFilters()
    addChoosableFileFilter(
        FileNameExtensionFilter("XLSX таблица", "xlsx")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppScreen() {
    var state by remember { mutableStateOf(0) }

    Column {
        SecondaryTabRow(selectedTabIndex = state) {
            Tab(
                selected = state == 0,
                onClick = { state = 0 },
                text = { Text(text = "Рабочая зона", maxLines = 2, overflow = TextOverflow.Ellipsis) }
            )
            Tab(
                selected = false,
                onClick = {
                    val status = fileChooser.showSaveDialog(null)
                    if(status == JFileChooser.APPROVE_OPTION) {
                        fileChooser.selectedFile?.let { file ->
                            val table = SignalRepository.savedSignalTable.value
                            workbook {
                                sheet("Итоги") {
                                    row {
                                        cell("Частота, Гц")
                                        cell("Амплитуда, дБм")
                                        cell("Амплитуда, мкВ")
                                    }

                                    table.entries.sortedBy { it.key }.forEach { signal ->
                                        row {
                                            cell(signal.key)
                                            cell(signal.value.dBm)
                                            cell(signal.value.voltage)
                                        }
                                    }
                                }
                            }.write(file.path)
                        }
                    }
                },
                text = { Text(text = "Экспорт", maxLines = 2, overflow = TextOverflow.Ellipsis) }
            )
            Tab(
                selected = state == 2,
                onClick = { state = 2 },
                text = { Text(text = "Настройки", maxLines = 2, overflow = TextOverflow.Ellipsis) }
            )
        }

        when (state) {
            0 -> WorkspaceTab()
            2 -> SettingsTab()
        }
    }
}