package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.levkopo.barsik.data.repositories.LogsRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SignalLogsCard(modifier: Modifier) {
    val logs by LogsRepository.logs.collectAsState()
    val scrollState = rememberLazyListState()
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) scrollState.animateScrollToItem(logs.size)
    }

    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        modifier = modifier
            .width(420.dp)
    ) {
        Column {
            Text(
                text = "Журнал событий",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(30.dp),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = scrollState
                ) {
                    items(logs.size) {
                        val log = logs[it]

                        Row {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(50.dp)
                                    .padding(end = 16.dp)
                                    .background(
                                        when (log.type) {
                                            LogsRepository.Log.Type.ERROR -> MaterialTheme.colorScheme.error
                                            LogsRepository.Log.Type.WARNING -> MaterialTheme.colorScheme.tertiary
                                            LogsRepository.Log.Type.INFO -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                            )

                            Column {
                                Text(log.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp)
                                Text(log.message, fontSize = 10.sp, lineHeight = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}