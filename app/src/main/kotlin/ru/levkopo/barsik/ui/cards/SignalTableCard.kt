package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.data.repositories.SignalRepository

@Composable
fun SignalTableCard() {
    val savedSignalTable by SignalRepository.savedSignalTable.collectAsState()

    Card(
        shape = RoundedCornerShape(size = 16.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ), modifier = Modifier.width(720.dp).height(420.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Частота, МГЦ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary).weight(1f)
                        .height(30.dp),
                )
                Text(
                    "дБм",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary).weight(1f)
                        .height(30.dp),
                )
                Text(
                    "мкВ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary).weight(1f)
                        .height(30.dp),
                )
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(savedSignalTable.entries.toList().sortedBy { it.key }) { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().background(
                            color = when {
                                index % 2 == 0 -> MaterialTheme.colorScheme.surfaceContainerLow
                                else -> MaterialTheme.colorScheme.surfaceContainerHigh
                            }
                        ).height(20.dp)
                    ) {
                        Text(
                            entry.key.toString(),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            entry.value.dBm.toString(),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            entry.value.voltage.toString(),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}