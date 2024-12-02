package ru.levkopo.barsik

import DSP.SignalMessage
import DSP.Signals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mdlaf.MaterialLookAndFeel
import mdlaf.themes.MaterialOceanicTheme
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel


private val barsik = Barsik()
private val mainCoroutineScope = CoroutineScope(Dispatchers.Main)

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    showSplash()

    UIManager.setLookAndFeel(MaterialLookAndFeel(MaterialOceanicTheme()))

    val mainFrame = JFrame("Барсик")
    mainFrame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    mainFrame.size = Dimension(800, 600)
    mainFrame.isResizable = false
    mainFrame.maximumSize = Dimension(800, 600)
    mainFrame.isVisible = true

    val mainBox = Box.createHorizontalBox()
    mainBox.border = BorderFactory.createEmptyBorder(14, 16, 16, 14)

    val rightBox = Box.createVerticalBox()
    rightBox.border = BorderFactory.createEmptyBorder(14, 16, 16, 14)

    val leftBox = Box.createVerticalBox()
    leftBox.border = BorderFactory.createEmptyBorder(14, 16, 16, 14)

    mainBox.add(leftBox)
    mainBox.add(rightBox)
    mainFrame.contentPane = mainBox

    val freqField = JTextField("5", 1)
    val widthField = JTextField("3", 1)
    val attField = JTextField("0", 1)

    val connectionButton = JButton()
    connectionButton.addActionListener {
        if(barsik.connectionStateFlow.value is Barsik.ConnectionState.CONNECTED) {
            barsik.disconnect()
        }else barsik.connect()
    }

    val startButton = JButton("Старт")
    startButton.addActionListener {
        barsik.startSend(
            frequency = freqField.text.toDouble(),
            attenuator = attField.text.toDouble(),
            width = widthField.text.toDouble(),
        )
    }

    val exportButton = JButton("Экспорт в XLSX")
    exportButton.addActionListener {

    }

    val errorTextArea = JTextArea(4, 4)
    errorTextArea.border = BorderFactory.createEmptyBorder(6, 8, 6, 8)
    errorTextArea.background = Color(255, 183, 183)

    val tableModel = DefaultTableModel()
    tableModel.setColumnIdentifiers(arrayOf("Частота", "Амплитуда"))

    val table = JTable(tableModel)
    rightBox.add(
        Box.createHorizontalBox().apply {
            add(connectionButton)
            add(exportButton)
        }
    )

    leftBox.add(
        Box.createVerticalBox().apply {
            add(Box.createHorizontalBox().apply {
                add(freqField)
                add(JLabel("кГЦ"))
            })

            add(Box.createHorizontalBox().apply {
                add(widthField)
                add(JLabel("кГЦ"))
            })

            add(Box.createHorizontalBox().apply {
                add(attField)
            })

            add(startButton)
        }
    )

    leftBox.add(Box.createVerticalBox().apply {
        border = BorderFactory.createEmptyBorder(8, 0, 8, 0)
        add(JScrollPane(errorTextArea))
    })

    rightBox.add(JScrollPane(table))

    mainCoroutineScope.launch {
        barsik.connectionStateFlow.collectLatest { state ->
            connectionButton.isEnabled =
                state is Barsik.ConnectionState.CONNECTED || state is Barsik.ConnectionState.IDLE || state is Barsik.ConnectionState.ERROR
            errorTextArea.text = ""
            errorTextArea.isVisible = state is Barsik.ConnectionState.ERROR
            exportButton.isEnabled = state is Barsik.ConnectionState.CONNECTED
            startButton.isEnabled = state is Barsik.ConnectionState.CONNECTED

            when (state) {
                is Barsik.ConnectionState.CONNECTED -> {
                    connectionButton.text = "Отсоединиться"
                }

                is Barsik.ConnectionState.IDLE -> {
                    connectionButton.text = "Подключится к серверу"
                }

                is Barsik.ConnectionState.CONNECTING -> {
                    connectionButton.text = "Попытка соединения"
                }

                is Barsik.ConnectionState.ERROR -> {
                    connectionButton.text = "Ошибка подключения"
                    errorTextArea.text = state.error.stackTraceToString()
                }
            }
        }
    }

//
//
//    println(ctrl.SendTest())
//    println(ctrl.SendSignalMessage())
}

fun SignalMessage.makeString(): String {
    return "SignalMessage{" +
            "frequency=" + frequency +
            ", attenuator=" + attenuator +
            ", width=" + width +
            ", signals=" + signals_.contentToString() +
            ", aab=" + aab +
            ", aac=" + aac +
            ", aad=" + aad +
            ", packetNumber=" + packetNumber +
            ", b=" + b +
            ", c=" + c +
            ", d=" + d +
            ", f=" + f +
            '}'
}

fun Signals.contentToString(): String {
    return "Signals{" +
            "a=" + a +
            ", b=" + b +
            ", c=" + c +
            ", d=" + d +
            ", e=" + e +
            ", f=" + f +
            ", g=" + g +
            ", h=" + h +
            '}'
}