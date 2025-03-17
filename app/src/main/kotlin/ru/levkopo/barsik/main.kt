package ru.levkopo.barsik

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import ru.levkopo.barsik.data.remote.SignalOrbManager
import ru.levkopo.barsik.ui.AppScreen
import java.awt.Dimension
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

fun showSplash() {
    val splashFrame = JFrame("Запуск программы")
    println(SignalOrbManager::class.java.getResource("/app_icon.png"))
    splashFrame.iconImage = ImageIcon(SignalOrbManager::class.java.getResource("/app_icon.png")).image
    splashFrame.contentPane = JLabel(
        "",
        ImageIcon(SignalOrbManager::class.java.getResource("/barsik.png")),
        SwingConstants.CENTER
    )

    splashFrame.size = Dimension(363, 283)
    splashFrame.maximumSize = splashFrame.size
    splashFrame.minimumSize = splashFrame.size
    splashFrame.isResizable = false
    splashFrame.isUndecorated = true
    splashFrame.isVisible = true

    runBlocking {
        delay(5000)
        splashFrame.isVisible = false
    }
}

@OptIn(ExperimentalResourceApi::class)
fun main() {
    showSplash()

    application {
        val windowState = rememberWindowState(
            width = 1820.dp,
            height = 1080.dp
        )

        Window(
            title = "Barsik",
            state = windowState,
            onCloseRequest = {
                runCatching {
                    SignalOrbManager.stop()
                }
                exitApplication()
            },
            icon = BitmapPainter(
                SignalOrbManager::class.java
                    .getResourceAsStream("/app_icon.png")!!
                    .readBytes()
                    .decodeToImageBitmap()
            )
        ) {
            MaterialTheme {
                AppScreen()
            }
        }
    }
}