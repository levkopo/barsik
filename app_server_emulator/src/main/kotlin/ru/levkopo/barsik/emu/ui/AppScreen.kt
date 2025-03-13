package ru.levkopo.barsik.emu.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import ru.levkopo.barsik.emu.ui.sections.ModulatorsScreen
import ru.levkopo.barsik.emu.ui.sections.ORBSection

private data class Section(
    val name: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit,
)

private val sections = arrayOf<Section>(
    Section(
        name = "ORB",
        icon = Icons.Default.Info,
        screen = { ORBSection() }
    ),
    Section(
        name = "Модуляторы",
        icon = Icons.Default.Call,
        screen = { ModulatorsScreen() }
    )
)

@Preview
@Composable
fun AppScreen() {
    var sectionIndex by remember { mutableStateOf(0) }
    val section = sections[sectionIndex]

    MaterialTheme {
        Row {
            NavigationRail {
                sections.forEachIndexed { index, section ->
                    NavigationRailItem(
                        selected = index == sectionIndex,
                        onClick = { sectionIndex = index },
                        icon = { Icon(section.icon, contentDescription = section.name) },
                        label = { Text(section.name) },
                    )
                }
            }

            Column {
                section.screen()
            }
        }

    }
}