package ru.levkopo.barsik.emu.modulators

import DSP.iq
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.levkopo.barsik.emu.modulators.base.BaseModulator
import java.io.File
import javax.imageio.ImageIO

class BadAppleModulator : BaseModulator() {
    val frameRate = 24
    var framesCount = 0
    var framesDone = MutableStateFlow(0)
    var currentFrame = MutableStateFlow(0)
    lateinit var frames: ArrayDeque<Array<iq>>

    init {
        CoroutineScope(Dispatchers.IO).launch {
            frames = ArrayDeque(
                File("./app_server_emulator/frames").let { file ->
                    framesCount = file.listFiles()?.size ?: 0
                    file.listFiles().map { frame ->
                        val image = ImageIO.read(frame)
                        val iQSignals = arrayListOf<iq>()
                        var blackPixels = 0
                        for (x in 0 until image.width) {
                            for (y in 0 until image.height) {
                                val color = image.getRGB(x, y)
                                val r = (color shr 16) and 0xff
                                val g = (color shr 8) and 0xff
                                val b = color and 0xff
                                val luminance = (0.299 * r + 0.587 * g + 0.114 * b)
                                if (luminance < 255 / 2) {
                                    blackPixels++
                                }
                            }
                        }

                        val isBlackMain = blackPixels >= (image.width * image.height) / 2
                        for (x in 0 until image.width) {
                            var minBlackPixel = image.height
                            var minWhitePixel = image.height
                            for (y in 0 until image.height) {
                                val color = image.getRGB(x, y)
                                val r = (color shr 16) and 0xff
                                val g = (color shr 8) and 0xff
                                val b = color and 0xff
                                val luminance = (0.299 * r + 0.587 * g + 0.114 * b)

                                if (luminance < 255 / 2) {
                                    minBlackPixel = minBlackPixel.coerceAtMost(y)
                                } else {
                                    minWhitePixel = minWhitePixel.coerceAtMost(y)
                                }
                            }

                            if (!isBlackMain) {
                                iQSignals.add(iq((image.height - minBlackPixel).toFloat(), 0.0f))
                            } else {
                                iQSignals.add(iq((image.height - minWhitePixel).toFloat(), 0.0f))
                            }
                        }

                        framesDone.value += 1
                        iQSignals.toTypedArray()
                    }
                }
            )
        }
    }

    override suspend fun run() {
        while (isRunning.value) {
            currentIQ = frames[currentFrame.value++]
            if (currentFrame.value > frames.size) {
                currentFrame.value = 0
            }

            delay((1000 / frameRate).toLong())
        }

        currentFrame.value = 0
    }

    @Composable
    override fun SettingsScreen() {
        val framesDone by this.framesDone.collectAsState(initial = 0)
        val currentFrame by this.currentFrame.collectAsState(initial = 0)

        Column {
            LinearProgressIndicator(
                progress = (framesDone.toDouble() / 1.coerceAtLeast(framesCount)).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Slider(
                value = currentFrame.toFloat() / framesCount,
                onValueChange = {
                    this@BadAppleModulator.currentFrame.value = (it * framesCount).toInt()
                }
            )
        }
    }
}