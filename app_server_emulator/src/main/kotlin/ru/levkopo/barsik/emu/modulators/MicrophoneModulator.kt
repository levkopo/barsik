package ru.levkopo.barsik.emu.modulators

import DSP.iq
import androidx.compose.runtime.Composable
import org.jtransforms.fft.DoubleFFT_1D
import ru.levkopo.barsik.emu.modulators.base.BaseModulator
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Модулятор сигналов из спектра сигнала микрофона
 */
class MicrophoneModulator: BaseModulator() {
    override suspend fun run() {
        try {
            // 1. Set up AudioFormat
            val sampleSizeInBits = 32
            val channels = 1 // Mono
            val bigEndian = false
            val sampleRate = 48000f
            val audioFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                sampleSizeInBits,
                channels,
                channels * (sampleSizeInBits / 8), // frameSize
                sampleRate, // frameRate
                bigEndian
            )

            // 2. Get TargetDataLine
            val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)

            if (!AudioSystem.isLineSupported(dataLineInfo)) {
                println("Line not supported: $dataLineInfo")
                return
            }

            val targetDataLine = AudioSystem.getLine(dataLineInfo) as TargetDataLine
            targetDataLine.open(audioFormat)

            // 3. Start capturing
            targetDataLine.start()

            // 4. Real-time processing loop
            val bufferSizeInSeconds = 0.01 // 10 milisseconds chunk size
            val bufferSizeInBytes = (sampleRate * audioFormat.frameSize * bufferSizeInSeconds).toInt() // Bytes for 1/100 second
            val buffer = ByteArray(bufferSizeInBytes)

            try {
                while (isRunning.value) {
                    val numBytesRead = targetDataLine.read(buffer, 0, buffer.size)

                    // Convert byte array to FloatArray (for each chunk)
                    val numBytesPerSample = audioFormat.sampleSizeInBits / 8
                    val floatArray = FloatArray(numBytesRead / (numBytesPerSample * channels))

                    for (i in 0 until floatArray.size) {
                        // Get the byte offset for the sample
                        val byteOffset = i * numBytesPerSample * channels

                        // Combine the bytes into an integer value
                        var sampleValue = 0
                        for (j in 0 until numBytesPerSample) {
                            val byteIndex = byteOffset + (if (bigEndian) j else (numBytesPerSample - 1 - j))
                            val byte = buffer[byteIndex].toInt() and 0xFF // Ensure unsigned interpretation
                            sampleValue = (sampleValue shl 8) or byte
                        }

                        if (audioFormat.encoding == AudioFormat.Encoding.PCM_SIGNED) {
                            val bitDepth = audioFormat.sampleSizeInBits
                            val maxValue = (1 shl (bitDepth - 1)) - 1
                            if (sampleValue > maxValue) {
                                sampleValue -= (1 shl bitDepth) // Convert to negative value if needed
                            }
                        }
                        floatArray[i] = sampleValue / 32768f // Adjust for floating range -1 to 1
                    }

                    currentIQ = convertToFFTIQ(floatArray, sampleRate.toDouble())
                }
            } catch (e: Exception) {
                println("Exception during capturing: ${e.message}")
            } finally {
                println("Microphone capturing stoped.")
                targetDataLine.stop()
                targetDataLine.close()
                println("Microphone line closed.")
            }
        } catch (e: LineUnavailableException) {
            println("Could not get audio line: ${e.message}")
        } catch (e: SecurityException) {
            println("Security exception: ${e.message}.  You may need to grant microphone access.")
        }
    }

    fun convertToFFTIQ(audioData: FloatArray, samplingRate: Double): Array<iq> {
        val iqSignals = mutableListOf<iq>()
        val amplitude = 1.0 // Carrier amplitude
        val fftLength = audioData.size

        val fft = DoubleFFT_1D(fftLength.toLong())
        val fftData = DoubleArray(fftLength)

        // Fill the real part
        for (i in audioData.indices) {
            fftData[i] = audioData[i].toDouble()
        }

        // Performing FFT
        fft.realForward(fftData)

        for (i in fftData.indices) {
            val time = i / samplingRate // Calculate time for the sample
            val modulatingSignal = fftData[i].toDouble() // Audio sample value (normalized)

            val iComponent = modulatingSignal * cos(2 * PI * time)
            val qComponent = modulatingSignal * sin(2 * PI * time)

            iqSignals.add(iq(iComponent.toFloat(), qComponent.toFloat()))
        }

        return iqSignals.toTypedArray()
    }

    @Composable
    override fun SettingsScreen() {

    }
}