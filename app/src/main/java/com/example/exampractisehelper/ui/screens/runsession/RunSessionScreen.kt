package com.example.exampractisehelper.ui.screens.runsession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import kotlinx.coroutines.delay

@Composable
fun RunSessionScreen(
    session: PracticeSession?,
    tasks: List<Task>,
    subtasksMap: Map<Int, List<Subtask>>,
    navController: NavController
) {
    // Session timer
    val sessionDuration = session?.totalDuration ?: 0
    var sessionTimer by remember { mutableStateOf(sessionDuration) }
    var sessionRunning by remember { mutableStateOf(false) }

    // Task timer
    var currentTaskIndex by remember { mutableStateOf(0) }
    val currentTask = tasks.getOrNull(currentTaskIndex)
    val taskDuration = currentTask?.taskDuration ?: 0
    var taskTimer by remember { mutableStateOf(taskDuration) }
    var taskRunning by remember { mutableStateOf(false) }

    // Subtask timer
    val subtasks = currentTask?.let { subtasksMap[it.taskId] } ?: emptyList()
    var currentSubtaskIndex by remember { mutableStateOf(0) }
    val currentSubtask = subtasks.getOrNull(currentSubtaskIndex)
    val nextSubtask = subtasks.getOrNull(currentSubtaskIndex + 1)
    val subtaskDuration = currentSubtask?.duration ?: 0
    var subtaskTimer by remember { mutableStateOf(subtaskDuration) }
    var subtaskRunning by remember { mutableStateOf(false) }

    // Session timer logic
    LaunchedEffect(sessionRunning) {
        while (sessionRunning && sessionTimer > 0) {
            delay(1000)
            sessionTimer--
        }
    }
    // Task timer logic
    LaunchedEffect(taskRunning) {
        while (taskRunning && taskTimer > 0) {
            delay(1000)
            taskTimer--
        }
    }
    // Subtask timer logic
    LaunchedEffect(subtaskRunning) {
        while (subtaskRunning && subtaskTimer > 0) {
            delay(1000)
            subtaskTimer--
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Session: ${session?.name ?: "Session"}", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                // Fancy timer for session (always visible)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%02d:%02d:%02d", sessionTimer / 3600, (sessionTimer % 3600) / 60, sessionTimer % 60),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.25, color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Current Task: ${currentTask?.text ?: "Done"}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                // Fancy timer for task (smaller than session timer)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%02d:%02d:%02d", taskTimer / 3600, (taskTimer % 3600) / 60, taskTimer % 60),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize, color = MaterialTheme.colorScheme.secondary)
                    )
                }
                Spacer(Modifier.height(12.dp))
                if (subtasks.isNotEmpty()) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                            Text("Current Subtask", style = MaterialTheme.typography.bodyMedium)
                            Text(currentSubtask?.name ?: "-", style = MaterialTheme.typography.bodyLarge)
                        }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("Next Subtask", style = MaterialTheme.typography.bodyMedium)
                            Text(nextSubtask?.name ?: "-", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Show current running subtask timer below the row, slightly less than before but still prominent
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d:%02d:%02d", subtaskTimer / 3600, (subtaskTimer % 3600) / 60, subtaskTimer % 60),
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 0.7, color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
        // Extra space for better visual separation
        Spacer(Modifier.height(24.dp))
        // Show all tasks as individual cards, max two per row
        Text("Available Tasks (Click to start)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        tasks.chunked(2).forEach { rowTasks ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                rowTasks.forEach { task ->
                    Card(
                        modifier = Modifier
                            .heightIn(min = 60.dp, max = 200.dp)
                            .weight(1f)
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left part: Task name (80% width)
                            Box(
                                modifier = Modifier.weight(0.8f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    task.text,
                                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            // Right part: Duration (vertical, 20% width, no label)
                            Box(
                                modifier = Modifier.weight(0.2f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                val h = (task.taskDuration ?: 0) / 3600
                                val m = ((task.taskDuration ?: 0) % 3600) / 60
                                val s = (task.taskDuration ?: 0) % 60
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("%dh".format(h), style = MaterialTheme.typography.bodyMedium)
                                    Text("%dm".format(m), style = MaterialTheme.typography.bodyMedium)
                                    Text("%ds".format(s), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
