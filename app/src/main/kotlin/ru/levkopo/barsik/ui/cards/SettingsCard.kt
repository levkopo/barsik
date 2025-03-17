package ru.levkopo.barsik.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.levkopo.barsik.configs.ApplicationConfig
import ru.levkopo.barsik.configs.ServerConfig
import ru.levkopo.barsik.configs.SignalConfig
import ru.levkopo.barsik.data.repositories.SignalRepository

@Composable
fun SettingsCard() {
    var linuxBoxIp by remember { mutableStateOf(ServerConfig.linuxBoxIp) }
    var linuxBoxOrbPort by remember { mutableStateOf(ServerConfig.linuxBoxOrbPort) }
    var linuxBoxPassword by remember { mutableStateOf(ServerConfig.linuxBoxPassword) }
    var linuxBoxUsername by remember { mutableStateOf(ServerConfig.linuxBoxUsername) }
    var linuxBoxSUPassword by remember { mutableStateOf(ServerConfig.linuxBoxPassword) }
    var profile by remember { mutableStateOf(ApplicationConfig.profile) }
    var serverStartupCommandName by remember { mutableStateOf(ApplicationConfig.serverStartupCommandName) }

    Card(
        shape = RoundedCornerShape(size = 16.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ), modifier = Modifier.width(332.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = linuxBoxIp,
                    onValueChange = { linuxBoxIp = it },
                    label = { Text("IP-адрес") },
                    modifier = Modifier
                        .width(188.dp),
                )

                OutlinedTextField(
                    value = linuxBoxOrbPort,
                    onValueChange = { linuxBoxOrbPort = it },
                    label = { Text("Порт") },
                    modifier = Modifier
                        .width(104.dp),
                )
            }
            OutlinedTextField(
                value = linuxBoxUsername,
                onValueChange = { linuxBoxUsername = it },
                label = { Text("Имя пользователя") },
                modifier = Modifier
                    .width(300.dp),
            )

            OutlinedTextField(
                value = linuxBoxPassword,
                onValueChange = { linuxBoxPassword = it },
                label = { Text("Пароль") },
                modifier = Modifier
                    .width(300.dp),
            )

            OutlinedTextField(
                value = linuxBoxSUPassword,
                onValueChange = { linuxBoxSUPassword = it },
                label = { Text("Пароль супер-пользователя") },
                modifier = Modifier
                    .width(300.dp),
            )

            HorizontalDivider()

            OutlinedTextField(
                value = profile,
                onValueChange = { profile = it },
                label = { Text("Путь до профиля") },
                modifier = Modifier
                    .width(300.dp),
            )
            OutlinedTextField(
                value = serverStartupCommandName,
                onValueChange = { serverStartupCommandName = it },
                label = { Text("Скрипт запуска") },
                modifier = Modifier
                    .width(300.dp),
            )
            Row {
                Spacer(Modifier.weight(4f))
                Button(
                    onClick = {
                        ApplicationConfig.serverStartupCommandName = serverStartupCommandName
                        ApplicationConfig.profile = profile
                        ServerConfig.linuxBoxIp = linuxBoxIp
                        ServerConfig.linuxBoxOrbPort = linuxBoxOrbPort
                        ServerConfig.linuxBoxPassword = linuxBoxPassword
                        ServerConfig.linuxBoxUsername = linuxBoxUsername
                        ServerConfig.linuxBoxSUPassword = linuxBoxSUPassword
                    }
                ) { Text(text = "Сохранить") }
            }
        }
    }
}