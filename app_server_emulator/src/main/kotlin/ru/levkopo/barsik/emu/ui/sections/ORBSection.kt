package ru.levkopo.barsik.emu.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.emu.launchORBD
import ru.levkopo.barsik.emu.poa.ports.transporters.TransporterCtrlUsesPortImpl

/**
 * Экран с информацией о состоянии ORB сервера
 */
@Composable
fun ORBSection() {
    val inputMessage by TransporterCtrlUsesPortImpl.inputMessage.collectAsState()
    val outputMessage by TransporterCtrlUsesPortImpl.outputMessage.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
    ) {
        Row {
            Button(onClick = {
                launchORBD()
            }) {
                Text("Initialize ORB")
            }
        }

        LazyVerticalGrid (
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    text = inputMessage,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Text(
                    text = outputMessage,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}