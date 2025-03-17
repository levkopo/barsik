package ru.levkopo.barsik.emu.modulators

import DSP.iq
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import ru.levkopo.barsik.emu.modulators.base.BaseModulator
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO

class BadAppleModulator : BaseModulator() {
    private val framesFiles by lazy {
        File("./app_server_emulator/frames").listFiles().toList().sortedBy {
            it.nameWithoutExtension
        }
    }
    private val framesCount by lazy { framesFiles.size }

    private val frameRate = 24
    private var framesDone = MutableStateFlow(0)
    private var currentFrame = MutableStateFlow(0)
    private val frames = ArrayDeque<Array<iq>>()

    fun generateFrames() {
        CoroutineScope(Dispatchers.IO).launch {
            framesFiles.forEach { frame ->
                val image = ImageIO.read(frame)
                val iQSignals = arrayListOf<iq>()
                iQSignals.add(iq(image.height.toFloat(), 0.0f))

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

                iQSignals.add(iq(image.height.toFloat(), 0.0f))
                framesDone.value += 1
                frames.addLast(iQSignals.toTypedArray())
            }
        }
    }

    override suspend fun run() {
        while (isRunning.value) {
            currentIQ = frames.getOrElse(currentFrame.value++) { arrayOf() }
            if (currentFrame.value > frames.size) {
                currentFrame.value = 0
            }

            delay((1000 / frameRate).toLong())
        }

        currentFrame.value = 0
    }

    @OptIn(ExperimentalResourceApi::class)
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

            Button(onClick = {
                generateFrames()
            }) {
                Text("Generate frames")
            }

            val image = framesFiles.getOrNull(currentFrame) ?: return

            Image(
                bitmap = FileInputStream(image).readAllBytes().decodeToImageBitmap(),
                contentDescription = "",
            )
        }
    }
}