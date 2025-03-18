package ru.levkopo.barsik.data

import ru.levkopo.barsik.configs.SignalConfig
import javax.sound.sampled.*
import kotlin.math.*

class RealTimeAmDemodulator(
    private val iqDataSource: IqDataSource,
    private val sampleRate: Double = 31250 * 2.0,
    private val bufferSize: Int = 32768
) : Thread() {
    private val audioFormat =
        AudioFormat(sampleRate.toFloat(), 16, 1, true, false) // 16-bit, mono, signed, little-endian
    private var sourceDataLine: SourceDataLine? = null
    private var isRunning: Boolean = false

    init {
        try {
            val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)
            sourceDataLine = AudioSystem.getLine(dataLineInfo) as SourceDataLine
            sourceDataLine?.open(audioFormat)
            sourceDataLine?.start()
        } catch (e: Exception) {
            println("Error initializing audio output: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun run() {
        super.run()
        while (isRunning) {
            val iqSamples = iqDataSource.getNextIqSamples(bufferSize)
            if (iqSamples.isEmpty()) {
                println("No more IQ samples.  Exiting.")
                break // End of stream
            }

            // 1. Demodulate
            val audioData = amDemodulation(iqSamples, SignalConfig.frequency, sampleRate)

            // 2. Scale
            val scaledAudioData = scaleAudioData(audioData, Short.MAX_VALUE.toDouble())

            // 3. Convert to PCM bytes
            val pcmData = convertToPCM(scaledAudioData)

            // 4. Write to audio output
            try {
                sourceDataLine?.write(pcmData, 0, pcmData.size)
            } catch (e: Exception) {
                println("Error writing to audio output: ${e.message}")
                e.printStackTrace()
                break
            }
        }

        try {
            sourceDataLine?.drain()
            sourceDataLine?.stop()
            sourceDataLine?.close()
        } catch (e: Exception) {
            println("Error stopping audio output: ${e.message}")
            e.printStackTrace()
        }
    }


    override fun start() {
        isRunning = true
        super.start()
    }

    fun stopRunning() {
        isRunning = false
    }


    // Function for AM Demodulation (Envelope Detection)
    private fun amDemodulation(
        iqSamples: List<Pair<Double, Double>>,
        carrierFrequency: Double,
        sampleRate: Double
    ): DoubleArray {
        val audioData = DoubleArray(iqSamples.size)

        for (i in iqSamples.indices) {
            // Calculate the magnitude (envelope) of the complex signal
            val magnitude = sqrt(iqSamples[i].first.pow(2) + iqSamples[i].second.pow(2))

            // Apply a DC Block filter to remove any DC offset.
            // A simple DC block is:  y[n] = x[n] - x[n-1] + a*y[n-1], where 'a' is close to 1.
            // Here, we implement it with a simple subtraction from the mean.

            audioData[i] = magnitude
        }

        //Remove dc offset.
        val mean = audioData.average()
        for (i in audioData.indices) {
            audioData[i] -= mean
        }

        return audioData
    }

    // Function to scale audio data to the range -max..max.
    private fun scaleAudioData(audioData: DoubleArray, maxAmplitude: Double): DoubleArray {
        val maxSignal = audioData.maxByOrNull { abs(it) } ?: 1.0
        val scaleFactor = maxAmplitude / maxSignal
        return audioData.map { it * scaleFactor }.toDoubleArray()
    }

    // Function to convert scaled audio data (doubles) to PCM bytes (16-bit)
    private fun convertToPCM(audioData: DoubleArray): ByteArray {
        val buffer = ByteArray(audioData.size * 2) // 2 bytes per sample (16-bit)
        for (i in audioData.indices) {
            val sample = audioData[i].toInt() // Truncate to integer (16-bit range)
            val index = i * 2
            buffer[index] = (sample and 0xFF).toByte()        // Low byte
            buffer[index + 1] = ((sample shr 8) and 0xFF).toByte() // High byte
        }
        return buffer
    }
}