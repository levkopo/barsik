package ru.levkopo.barsik

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.levkopo.barsik.ui.AppScreen


@OptIn(ExperimentalStdlibApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
    ) {
        MaterialTheme {
            AppScreen()
        }
    }
}