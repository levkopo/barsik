package ru.levkopo.barsik

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jacorb.orb.CDRInputStream
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel


private val barsik = Barsik()
private val mainCoroutineScope = CoroutineScope(Dispatchers.Main)

@OptIn(ExperimentalStdlibApi::class)
fun main() {
//    val outputStream = CDRInputStream(byteArrayOf(
//        0x07, 0x00, 0x00, 0x00, 0x53, 0x4e, 0x54, 0x65, 0x73, 0x74,
//        0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00,
//        0x70, 0x72, 0x6f, 0x66, 0x69, 0x6c, 0x65, 0x00, 0x12, 0x00,
//        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x15, 0x00, 0x00, 0x00,
//        0x61, 0x63, 0x65, 0x6e, 0x74, 0x65, 0x72, 0x2e, 0x63, 0x6f,
//        0x6e, 0x66, 0x2f, 0x73, 0x61, 0x64, 0x2e, 0x78, 0x6d, 0x6c,
//        0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00,
//        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
//        0x00
//    ))
//
//
//    println(outputStream.read_Object())
//    println(outputStream.read_abstract_interface())

    showSplash()

    val mainFrame = JFrame("Барсик")
    mainFrame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    mainFrame.size = Dimension(800, 600)
    mainFrame.isResizable = false
    mainFrame.maximumSize = Dimension(800, 600)
    mainFrame.isVisible = true

    val mainBox = Box.createVerticalBox()
    mainBox.border = BorderFactory.createEmptyBorder(14, 16, 16, 14)
    mainFrame.contentPane = mainBox

    val connectionButton = JButton()
    connectionButton.addActionListener {
        if(barsik.state.value is Barsik.State.CONNECTED) {
            barsik.stop()
        }else barsik.start()
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
    mainBox.add(
        Box.createHorizontalBox().apply {
            add(connectionButton)
            add(exportButton)
        }
    )

    mainBox.add(Box.createVerticalBox().apply {
        border = BorderFactory.createEmptyBorder(8, 0, 8, 0)
        add(JScrollPane(errorTextArea))
    })

    mainBox.add(JScrollPane(table))

    mainCoroutineScope.launch {
        barsik.state.collectLatest { state ->
            connectionButton.isEnabled =
                state is Barsik.State.CONNECTED || state is Barsik.State.IDLE || state is Barsik.State.ERROR
            errorTextArea.text = ""
            errorTextArea.isVisible = state is Barsik.State.ERROR
            exportButton.isEnabled = state is Barsik.State.CONNECTED

            when (state) {
                is Barsik.State.CONNECTED -> {
                    connectionButton.text = "Отсоединиться"
                }

                is Barsik.State.IDLE -> {
                    connectionButton.text = "Подключится к серверу"
                }

                is Barsik.State.CONNECTING -> {
                    connectionButton.text = "Попытка соединения"
                }

                is Barsik.State.ERROR -> {
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