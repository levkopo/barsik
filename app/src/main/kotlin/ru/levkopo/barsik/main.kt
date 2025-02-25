package ru.levkopo.barsik

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


private val barsik = Barsik()
private val mainCoroutineScope = CoroutineScope(Dispatchers.Main)

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(
            onClick = { text = "Hello, Desktop!" },
            modifier = Modifier.testTag("button")
        ) {
            Text(text)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}