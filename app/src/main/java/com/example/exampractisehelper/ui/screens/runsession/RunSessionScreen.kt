package com.example.exampractisehelper.ui.screens.runsession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun RunSessionScreen(
    sessionName: String,
    tasks: List<String>, // For simplicity, just task names
    navController: NavController
) {
    var currentTaskIndex by remember { mutableStateOf(0) }
    var timerSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            timerSeconds++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Session: $sessionName", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Task: ${tasks.getOrNull(currentTaskIndex) ?: "Done"}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(32.dp))
        Text("Timer: ${timerSeconds / 3600}h ${(timerSeconds % 3600) / 60}m ${timerSeconds % 60}s", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        Row {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pause" else "Start")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = {
                isRunning = false
                timerSeconds = 0
            }) {
                Text("Reset")
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = {
            if (currentTaskIndex < tasks.size - 1) {
                currentTaskIndex++
                timerSeconds = 0
            } else {
                navController.popBackStack()
            }
        }) {
            Text(if (currentTaskIndex < tasks.size - 1) "Next Task" else "Finish Session")
        }
    }
}

