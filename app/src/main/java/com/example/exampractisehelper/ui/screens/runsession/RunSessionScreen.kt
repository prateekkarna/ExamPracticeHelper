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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.exampractisehelper.ui.theme.ExamPractiseHelperTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
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
                        text = String.format(Locale.getDefault(), "%02d:%02d:%02d", sessionTimer / 3600, (sessionTimer % 3600) / 60, sessionTimer % 60),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.25, color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
        // Only show the task timer card if there is at least one task
        if (tasks.isNotEmpty()) {
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
                            text = String.format(Locale.getDefault(), "%02d:%02d:%02d", taskTimer / 3600, (taskTimer % 3600) / 60, taskTimer % 60),
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
                                text = String.format(Locale.getDefault(), "%02d:%02d:%02d", subtaskTimer / 3600, (subtaskTimer % 3600) / 60, subtaskTimer % 60),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = MaterialTheme.typography.displayLarge.fontSize * 0.7, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
            // Move available tasks card below the task timer card
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
        // Play/Stop controls at the bottom
        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Play/Pause button
            IconButton(
                onClick = {
                    sessionRunning = !sessionRunning
                    taskRunning = !taskRunning
                    if (subtasks.isNotEmpty()) {
                        subtaskRunning = !subtaskRunning
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                if (sessionRunning) {
                    Icon(
                        imageVector = Icons.Filled.Pause,
                        contentDescription = "Pause",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(Modifier.width(32.dp))
            // Stop button
            IconButton(
                onClick = {
                    sessionRunning = false
                    taskRunning = false
                    subtaskRunning = false
                    // Optionally reset timers to initial values
                    sessionTimer = sessionDuration
                    taskTimer = taskDuration
                    subtaskTimer = subtaskDuration
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "Stop",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunSessionScreenPreview() {
    ExamPractiseHelperTheme {
        // Preview with dummy data
        val dummySession = PracticeSession(
            sessionId = 1,
            name = "Dummy Session",
            isTimed = true,
            totalDuration = 3600,
            loopEnabled = false,
            loopCount = 1,
            isSimpleSession = false
        )
        val dummyTasks = listOf(
            Task(
                taskId = 1,
                sessionId = 1,
                text = "Task 1",
                hasSubtasks = true,
                taskDuration = 600,
                typeLabel = "Type A"
            ),
            Task(
                taskId = 2,
                sessionId = 1,
                text = "Task 2",
                hasSubtasks = false,
                taskDuration = 1200,
                typeLabel = "Type B"
            ),
            Task(
                taskId = 3,
                sessionId = 1,
                text = "Task 3",
                hasSubtasks = true,
                taskDuration = 900,
                typeLabel = "Type C"
            )
        )
        val dummySubtasksMap = mapOf(
            1 to listOf(
                Subtask(
                    subtaskId = 1,
                    taskId = 1,
                    name = "Subtask 1.1",
                    duration = 300
                ),
                Subtask(
                    subtaskId = 2,
                    taskId = 1,
                    name = "Subtask 1.2",
                    duration = 300
                )
            ),
            3 to listOf(
                Subtask(
                    subtaskId = 3,
                    taskId = 3,
                    name = "Subtask 3.1",
                    duration = 450
                ),
                Subtask(
                    subtaskId = 4,
                    taskId = 3,
                    name = "Subtask 3.2",
                    duration = 450
                )
            )
        )
        RunSessionScreen(
            session = dummySession,
            tasks = dummyTasks,
            subtasksMap = dummySubtasksMap,
            navController = NavController(LocalContext.current)
        )
    }
}
