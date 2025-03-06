package ru.levkopo.barsik

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.levkopo.barsik.data.remote.SignalOrbManager
import ru.levkopo.barsik.ui.AppScreen


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        SignalOrbManager.start()
            .onSuccess {
                println("Application started successfully")
            }
            .onFailure { error ->
                error.printStackTrace()
            }
    }

    application {
        Window(
            onCloseRequest = {
                SignalOrbManager.stop()
                exitApplication()
            },
        ) {
            MaterialTheme {
                AppScreen()
            }
        }
    }
}