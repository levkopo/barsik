package ru.levkopo.barsik

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.awt.Dimension
import javax.swing.*


fun showSplash() {
    val splashFrame = JFrame("Запуск программы")
    splashFrame.contentPane = JLabel(
        "",
        ImageIcon(Barsik::class.java.getResource("/barsik.png")),
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