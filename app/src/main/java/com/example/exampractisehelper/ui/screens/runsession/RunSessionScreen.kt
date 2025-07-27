package com.example.exampractisehelper.ui.screens.runsession

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RunSessionScreen(
    session: PracticeSession?,
    tasks: List<Task>,
    subtasksMap: Map<Int, List<Subtask>>,
    navController: NavController
) {
    // Session timer
    val sessionDuration = session?.totalDuration ?: 0
    var sessionTimer by remember { mutableStateOf(if (sessionDuration > 0) sessionDuration else 0) }
    var sessionRunning by remember { mutableStateOf(false) }
    // Track if timer should count up or down
    val sessionCountsDown = sessionDuration > 0

    // Task timer
    var currentTaskIndex by remember { mutableStateOf<Int?>(null) }
    val currentTask = currentTaskIndex?.let { tasks.getOrNull(it) }
    val taskName = currentTask?.text ?: "Select a Task"
    val taskDuration = currentTask?.taskDuration ?: 0
    var taskTimer by remember { mutableStateOf(0) }
    var taskRunning by remember { mutableStateOf(false) }

    // Subtask timer
    val subtasks = currentTask?.let { subtasksMap[it.taskId] } ?: emptyList()
    var currentSubtaskIndex by remember { mutableStateOf<Int?>(null) }
    val currentSubtask = currentSubtaskIndex?.let { subtasks.getOrNull(it) }
    val nextSubtask = currentSubtaskIndex?.let { subtasks.getOrNull(it + 1) }
    val subtaskDuration = currentSubtask?.duration ?: 0
    var subtaskTimer by remember { mutableStateOf(0) }
    var subtaskRunning by remember { mutableStateOf(false) }

    // Add state for selected task
    var selectedTaskIndex by remember { mutableStateOf<Int?>(null) }
    // Add state for tooltip visibility
    var showSelectTaskTooltip by remember { mutableStateOf(false) }

    // Session timer logic
    LaunchedEffect(sessionRunning) {
        while (sessionRunning) {
            delay(1000)
            if (sessionCountsDown) {
                sessionTimer-- // allow negative values
            } else {
                sessionTimer++
            }
        }
    }
    // Task timer logic
    LaunchedEffect(taskRunning) {
        while (taskRunning) {
            delay(1000)
            if (currentTask?.taskDuration != null && currentTask.taskDuration > 0) {
                taskTimer-- // allow negative values
            } else {
                taskTimer++
            }
            // If task timer is negative, stop subtask timer
            if (taskTimer < 0) {
                subtaskRunning = false
            }
        }
    }
    // Subtask timer logic
    LaunchedEffect(subtaskRunning, currentSubtaskIndex, subtaskTimer) {
        if (subtaskRunning) {
            while (subtaskRunning && subtaskTimer > 0) {
                delay(1000)
                subtaskTimer--
                // When subtask timer reaches 0, move to next subtask if available
                if (subtaskTimer == 0 && currentSubtaskIndex != null) {
                    val nextIndex = currentSubtaskIndex!! + 1
                    if (nextIndex < subtasks.size) {
                        currentSubtaskIndex = nextIndex
                        subtaskTimer = subtasks[nextIndex].duration ?: 0
                    } else {
                        subtaskRunning = false // No more subtasks
                    }
                }
            }
        }
    }

    // Timer font sizes
    val smallestTimerSize = MaterialTheme.typography.displaySmall.fontSize
    val mediumTimerSize = MaterialTheme.typography.displayMedium.fontSize * 1.3f
    val largestTimerSize = if (sessionTimer < 0 || taskTimer < 0) MaterialTheme.typography.displayLarge.fontSize * 1.2f else MaterialTheme.typography.displayLarge.fontSize * 1.3f

    // Determine which timers are visible
    val showSubtaskTimer = subtasks.isNotEmpty()
    val showTaskTimer = tasks.isNotEmpty()
    val showSessionTimer = true

    // Assign font sizes based on preference
    val sessionTimerFontSize = when {
        showSubtaskTimer && showTaskTimer -> smallestTimerSize
        showTaskTimer -> mediumTimerSize
        else -> largestTimerSize
    }
    val taskTimerFontSize = when {
        showSubtaskTimer -> mediumTimerSize
        showTaskTimer -> largestTimerSize
        else -> mediumTimerSize
    }
    val subtaskTimerFontSize = if (showSubtaskTimer) largestTimerSize else mediumTimerSize

    // Timer color logic
    val sessionTimerColor = if (sessionTimer < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val taskTimerColor = if (taskTimer < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tasks.isNotEmpty()) {
            // Show main session timer card
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentTaskIndex == null) {
                                    showSelectTaskTooltip = true
                                } else {
                                    sessionRunning = !sessionRunning
                                    taskRunning = !taskRunning
                                    if (subtasks.isNotEmpty()) {
                                        subtaskRunning = !subtaskRunning
                                    }
                                }
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            val playTint = if (currentTaskIndex != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            if (sessionRunning) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(80.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = playTint,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text("Session Timer", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                // Reset all data to initial value
                                sessionRunning = false
                                taskRunning = false
                                subtaskRunning = false
                                sessionTimer = if (sessionCountsDown) sessionDuration else 0
                                currentTaskIndex = null
                                taskTimer = 0
                                currentSubtaskIndex = null
                                subtaskTimer = 0
                                selectedTaskIndex = null
                            },
                            modifier = Modifier.size(56.dp),
                            enabled = currentTaskIndex != null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    // Session timer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (sessionTimer < 0) "-" else "",
                                kotlin.math.abs(sessionTimer) / 3600,
                                (kotlin.math.abs(sessionTimer) % 3600) / 60,
                                kotlin.math.abs(sessionTimer) % 60),
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = sessionTimerFontSize, color = sessionTimerColor)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            // Only show the task timer card if there is at least one task
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentTask != null) {
                        Text(
                            "Current Task: $taskName",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            "Select a Task",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Fancy timer for task (smaller than session timer)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (taskTimer < 0) "-" else "",
                                kotlin.math.abs(taskTimer) / 3600,
                                (kotlin.math.abs(taskTimer) % 3600) / 60,
                                kotlin.math.abs(taskTimer) % 60),
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = taskTimerFontSize, color = taskTimerColor)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    if (subtasks.isNotEmpty()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Current Subtask",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        currentSubtask?.name ?: "-",
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Next Subtask",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        nextSubtask?.name ?: "-",
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        // Show current running subtask timer below the row, slightly less than before but still prominent
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d:%02d", subtaskTimer / 3600, (subtaskTimer % 3600) / 60, subtaskTimer % 60),
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = subtaskTimerFontSize, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
            // AlertDialog for play button tooltip
            if (showSelectTaskTooltip) {
                AlertDialog(
                    onDismissRequest = { showSelectTaskTooltip = false },
                    confirmButton = {
                        TextButton(onClick = { showSelectTaskTooltip = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("No Task Selected") },
                    text = { Text("Please select a task to start the session.") }
                )
            }
            // Move available tasks card below the task timer card
            Text("Available Tasks (Click to start)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            tasks.chunked(2).forEach { rowTasks ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    rowTasks.forEachIndexed { idx, task ->
                        val globalIndex = tasks.indexOf(task)
                        Card(
                            modifier = Modifier
                                .heightIn(min = 60.dp, max = 200.dp)
                                .weight(1f)
                                .padding(4.dp)
                                .clickable {
                                    currentTaskIndex = globalIndex
                                    // Reset timers for selected task
                                    taskTimer = task.taskDuration ?: 0
                                    currentSubtaskIndex = if (subtasksMap[task.taskId]?.isNotEmpty() == true) 0 else null
                                    subtaskTimer = subtasksMap[task.taskId]?.getOrNull(0)?.duration ?: 0
                                    // sessionTimer should NOT reset here
                                },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    task.text,
                                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                val h = (task.taskDuration ?: 0) / 3600
                                val m = ((task.taskDuration ?: 0) % 3600) / 60
                                val s = (task.taskDuration ?: 0) % 60
                                Text(
                                    text = String.format("%dh %dm %ds", h, m, s),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Only session timer, show big timer and play/stop below
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Session Timer", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                                if (sessionTimer < 0) "-" else "",
                                kotlin.math.abs(sessionTimer) / 3600,
                                (kotlin.math.abs(sessionTimer) % 3600) / 60,
                                kotlin.math.abs(sessionTimer) % 60),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = largestTimerSize, color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                sessionRunning = !sessionRunning
                            },
                            modifier = Modifier.size(72.dp)
                        ) {
                            if (sessionRunning) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(32.dp))
                        IconButton(
                            onClick = {
                                // Reset all data to initial value
                                sessionRunning = false
                                taskRunning = false
                                subtaskRunning = false
                                sessionTimer = if (sessionCountsDown) sessionDuration else 0
                                currentTaskIndex = null
                                taskTimer = 0
                                currentSubtaskIndex = null
                                subtaskTimer = 0
                                selectedTaskIndex = null
                            },
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }
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
