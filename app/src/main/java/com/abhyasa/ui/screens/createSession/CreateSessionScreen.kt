package com.abhyasa.ui.screens.createSession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.abhyasa.ui.screens.createSession.components.AddTaskComponent
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhyasa.viewmodel.CreateSessionViewModel
import androidx.compose.ui.text.font.FontWeight

data class Subtask(val name: String, val hours: String, val minutes: String, val seconds: String)

@Composable
fun CreateSessionScreen(
    navController: NavController,
    viewModel: CreateSessionViewModel = hiltViewModel()
) {
    var sessionName by remember { mutableStateOf("") }
    var addTasks by remember { mutableStateOf<Boolean?>(null) }
    var sessionDuration by remember { mutableStateOf("") }
    var sessionHours by remember { mutableStateOf("") }
    var sessionMinutes by remember { mutableStateOf("") }
    var sessionSeconds by remember { mutableStateOf("") }
    var taskName by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf(listOf<Triple<String, List<Subtask>, Triple<String, String, String>?>>()) }
    var currentTaskName by remember { mutableStateOf("") }
    var currentTaskHours by remember { mutableStateOf("") }
    var currentTaskMinutes by remember { mutableStateOf("") }
    var currentTaskSeconds by remember { mutableStateOf("") }
    var currentSubtaskName by remember { mutableStateOf("") }
    var subtasksForCurrentTask by remember { mutableStateOf(listOf<Subtask>()) } // name, hours, min, sec
    var currentSubtaskHours by remember { mutableStateOf("") }
    var currentSubtaskMinutes by remember { mutableStateOf("") }
    var currentSubtaskSeconds by remember { mutableStateOf("") }
    var askSubtask by remember { mutableStateOf<Boolean?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var isEditingTask by remember { mutableStateOf(false) }
    var editingTaskIndex by remember { mutableStateOf(-1) }
    var editingTaskBackup by remember { mutableStateOf<Triple<String, List<Subtask>, Triple<String, String, String>?>?>(null) }
    val scrollState = rememberScrollState()
    val tasksWithSubtasks = tasks.map { (taskName, subtasks, timerTriple) ->
        val hasSubtasks = subtasks.isNotEmpty()
        val taskDuration = if (hasSubtasks) {
            subtasks.sumOf {
                (it.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                (it.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                (it.seconds.ifBlank { "0" }.toIntOrNull() ?: 0)
            }
        } else if (timerTriple != null) {
            val (h, m, s) = timerTriple
            (h.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
            (m.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
            (s.ifBlank { "0" }.toIntOrNull() ?: 0)
        } else 0
        val task = com.abhyasa.data.entities.Task(
            taskId = 0,
            sessionId = 0, // Will be set in repository
            text = taskName,
            hasSubtasks = hasSubtasks,
            taskDuration = taskDuration,
            typeLabel = ""
        )
        val subtaskEntities = subtasks.map {
            com.abhyasa.data.entities.Subtask(
                subtaskId = 0,
                taskId = 0, // Will be set in repository
                name = it.name,
                duration = ((it.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 + (it.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 + (it.seconds.ifBlank { "0" }.toIntOrNull() ?: 0))
            )
        }
        task to subtaskEntities
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Create Practice Session",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        OutlinedTextField(
                            value = sessionName,
                            onValueChange = { sessionName = it },
                            label = { Text("Session Name* (eg. HLT/FLT)") },
                            isError = sessionName.isEmpty(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (sessionName.isEmpty()) {
                            Text(
                                text = "Session name is required",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Do you want to add tasks to this session?", style = MaterialTheme.typography.titleMedium)
                        Row(
                            Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = addTasks == true,
                                onClick = { addTasks = true }
                            )
                            Text("Yes", Modifier.padding(start = 4.dp, end = 16.dp).align(Alignment.CenterVertically))
                            RadioButton(
                                selected = addTasks == false,
                                onClick = { addTasks = false }
                            )
                            Text("No", Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
                        }
                        // Show timer only if "No" is selected
                        if (addTasks == false) {
                            Text("Session Duration*", style = MaterialTheme.typography.titleMedium)
                            val isSessionMinutesValid = sessionMinutes.isBlank() || (sessionMinutes.toIntOrNull() ?: 0) in 0..59
                            val isSessionSecondsValid = sessionSeconds.isBlank() || (sessionSeconds.toIntOrNull() ?: 0) in 0..59
                            val isSessionDurationValid = sessionHours.isNotBlank() || sessionMinutes.isNotBlank() || sessionSeconds.isNotBlank()
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = sessionHours,
                                    onValueChange = { sessionHours = it.filter { c -> c.isDigit() } },
                                    label = { Text("HH") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null,
                                    isError = !isSessionDurationValid
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = sessionMinutes,
                                    onValueChange = { sessionMinutes = it.filter { c -> c.isDigit() } },
                                    label = { Text("MM") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null,
                                    isError = !isSessionMinutesValid
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = sessionSeconds,
                                    onValueChange = { sessionSeconds = it.filter { c -> c.isDigit() } },
                                    label = { Text("SS") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null,
                                    isError = !isSessionSecondsValid
                                )
                            }
                            if (!isSessionDurationValid) {
                                Text(
                                    text = "Session duration is required (enter at least one field)",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                                )
                            }
                            if (!isSessionMinutesValid && sessionMinutes.isNotBlank()) {
                                Text(
                                    text = "Minutes must be between 0 and 59",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                                )
                            }
                            if (!isSessionSecondsValid && sessionSeconds.isNotBlank()) {
                                Text(
                                    text = "Seconds must be between 0 and 59",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(24.dp))
                if (addTasks == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Tasks to Session", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.weight(1f))
                                Button(onClick = {
                                    showTaskDialog = true
                                    isEditingTask = false
                                    // Reset timer fields when adding new task
                                    currentTaskHours = ""
                                    currentTaskMinutes = ""
                                    currentTaskSeconds = ""
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (tasks.isNotEmpty()) {
                                Text("Tasks:", style = MaterialTheme.typography.titleSmall)
                                // Show a scrollable list of tasks as cards
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 250.dp)
                                ) {
                                    itemsIndexed(tasks) { idx, (taskName, subtasks, timerTriple) ->
                                        // Find the corresponding Task entity for this taskName
                                        val taskEntity = tasksWithSubtasks.find { it.first.text == taskName }?.first
                                        val totalTaskSeconds = if (subtasks.isNotEmpty()) subtasks.sumOf {
                                            (it.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                            (it.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                            (it.seconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                                        } else taskEntity?.taskDuration
                                        val formattedTaskDuration = totalTaskSeconds?.let { secs ->
                                            val h = secs / 3600
                                            val m = (secs % 3600) / 60
                                            val s = secs % 60
                                            listOf(
                                                if (h > 0) "${h}h" else null,
                                                if (m > 0) "${m}m" else null,
                                                if (s > 0) "${s}s" else null
                                            ).filterNotNull().joinToString(" ")
                                        }
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(Modifier.padding(12.dp)) {
                                                // Task name, edit, delete in one row
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                    Text(
                                                        text = taskName,
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        ),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Spacer(Modifier.width(16.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(onClick = {
                                                            // Edit task logic
                                                            currentTaskName = taskName
                                                            askSubtask = if (subtasks.isNotEmpty()) true else false
                                                            subtasksForCurrentTask = subtasks
                                                            showTaskDialog = true
                                                            isEditingTask = true
                                                            editingTaskIndex = idx
                                                            editingTaskBackup = Triple(taskName, subtasks, tasks[idx].third)
                                                            if (subtasks.isEmpty()) {
                                                                val timerTriple = tasks[idx].third
                                                                currentTaskHours = timerTriple?.first ?: ""
                                                                currentTaskMinutes = timerTriple?.second ?: ""
                                                                currentTaskSeconds = timerTriple?.third ?: ""
                                                            } else {
                                                                currentTaskHours = ""
                                                                currentTaskMinutes = ""
                                                                currentTaskSeconds = ""
                                                            }
                                                            currentSubtaskName = ""
                                                            currentSubtaskHours = ""
                                                            currentSubtaskMinutes = ""
                                                            currentSubtaskSeconds = ""
                                                            // Remove the task from the list so it can be re-added after editing
                                                            tasks = tasks.toMutableList().also { it.removeAt(idx) }
                                                        }) {
                                                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                                                        }
                                                        IconButton(onClick = {
                                                            tasks = tasks.toMutableList().also { it.removeAt(idx) }
                                                        }) {
                                                            Icon(Icons.Default.Delete, contentDescription = "Remove Task")
                                                        }
                                                    }
                                                }
                                                // Task Duration row
                                                if (formattedTaskDuration != null) {
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text(
                                                            text = "Task Duration",
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                color = MaterialTheme.colorScheme.primary,
                                                                fontWeight = FontWeight.Medium
                                                            ),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        val h = totalTaskSeconds?.div(3600) ?: 0
                                                        val m = totalTaskSeconds?.rem(3600)?.div(60) ?: 0
                                                        val s = totalTaskSeconds?.rem(60) ?: 0
                                                        val taskDurationFormat = String.format("%dh %dm %ds", h, m, s)
                                                        Text(
                                                            text = taskDurationFormat,
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                color = MaterialTheme.colorScheme.primary,
                                                                fontWeight = FontWeight.Medium
                                                            ),
                                                            modifier = Modifier.align(Alignment.CenterVertically)
                                                        )
                                                    }
                                                }
                                                // Subtasks section
                                                if (subtasks.isNotEmpty()) {
                                                    Text(
                                                        text = "Subtasks",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            color = MaterialTheme.colorScheme.secondary,
                                                            fontWeight = FontWeight.ExtraBold
                                                        ),
                                                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                                                    )
                                                    Column(Modifier.padding(start = 8.dp)) {
                                                        subtasks.forEachIndexed { sIdx, subtask ->
                                                            val h = subtask.hours.ifBlank { "0" }.toIntOrNull() ?: 0
                                                            val m = subtask.minutes.ifBlank { "0" }.toIntOrNull() ?: 0
                                                            val s = subtask.seconds.ifBlank { "0" }.toIntOrNull() ?: 0
                                                            val subtaskDuration = String.format("%dh %dm %ds", h, m, s)
                                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                                Text(
                                                                    text = "- ",
                                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                                        color = MaterialTheme.colorScheme.secondary,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                )
                                                                Text(
                                                                    text = subtask.name,
                                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                                        color = MaterialTheme.colorScheme.secondary,
                                                                        fontWeight = FontWeight.Normal
                                                                    ),
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                Text(
                                                                    text = subtaskDuration,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.secondary,
                                                                        fontWeight = FontWeight.Medium
                                                                    ),
                                                                    modifier = Modifier.align(Alignment.CenterVertically)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Popup dialog for adding a task
                            if (showTaskDialog) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showTaskDialog = false
                                        if (isEditingTask && editingTaskBackup != null && editingTaskIndex >= 0) {
                                            tasks = tasks.toMutableList().apply { add(editingTaskIndex, editingTaskBackup!!) }
                                        }
                                        isEditingTask = false
                                        editingTaskIndex = -1
                                        editingTaskBackup = null
                                        currentTaskName = ""
                                        subtasksForCurrentTask = listOf()
                                        askSubtask = null
                                        currentTaskHours = ""
                                        currentTaskMinutes = ""
                                        currentTaskSeconds = ""
                                        currentSubtaskName = ""
                                        currentSubtaskHours = ""
                                        currentSubtaskMinutes = ""
                                        currentSubtaskSeconds = ""
                                    },
                                    title = { Text(if (isEditingTask) "Edit Task" else "Add Task") },
                                    text = {
                                        Column {
                                            AddTaskComponent(
                                                taskName = currentTaskName,
                                                onTaskNameChange = {
                                                    currentTaskName = it
                                                    if (it.isEmpty()) {
                                                        askSubtask = null
                                                        subtasksForCurrentTask = listOf()
                                                        currentTaskHours = ""
                                                        currentTaskMinutes = ""
                                                        currentTaskSeconds = ""
                                                        currentSubtaskName = ""
                                                        currentSubtaskHours = ""
                                                        currentSubtaskMinutes = ""
                                                        currentSubtaskSeconds = ""
                                                    }
                                                },
                                                taskHours = currentTaskHours,
                                                onTaskHoursChange = { currentTaskHours = it.filter { c -> c.isDigit() } },
                                                taskMinutes = currentTaskMinutes,
                                                onTaskMinutesChange = { currentTaskMinutes = it.filter { c -> c.isDigit() } },
                                                taskSeconds = currentTaskSeconds,
                                                onTaskSecondsChange = { currentTaskSeconds = it.filter { c -> c.isDigit() } },
                                                buttonText = if (isEditingTask) "Update Task" else "Add Task",
                                                onCancel = {
                                                    showTaskDialog = false
                                                    if (isEditingTask && editingTaskBackup != null && editingTaskIndex >= 0) {
                                                        tasks = tasks.toMutableList().apply { add(editingTaskIndex, editingTaskBackup!!) }
                                                    }
                                                    isEditingTask = false
                                                    editingTaskIndex = -1
                                                    editingTaskBackup = null
                                                    currentTaskName = ""
                                                    subtasksForCurrentTask = listOf()
                                                    askSubtask = null
                                                    currentTaskHours = ""
                                                    currentTaskMinutes = ""
                                                    currentTaskSeconds = ""
                                                    currentSubtaskName = ""
                                                    currentSubtaskHours = ""
                                                    currentSubtaskMinutes = ""
                                                    currentSubtaskSeconds = ""
                                                },
                                                onAddTask = {
                                                    if (currentTaskName.isNotBlank()) {
                                                        val timerTriple = if (askSubtask == true) null else Triple(currentTaskHours, currentTaskMinutes, currentTaskSeconds)
                                                        if (isEditingTask && editingTaskIndex >= 0) {
                                                            tasks = tasks.toMutableList().apply { add(editingTaskIndex, Triple(currentTaskName, subtasksForCurrentTask, timerTriple)) }
                                                        } else {
                                                            tasks = tasks + Triple(currentTaskName, subtasksForCurrentTask, timerTriple)
                                                        }
                                                        isEditingTask = false
                                                        editingTaskIndex = -1
                                                        editingTaskBackup = null
                                                        currentTaskName = ""
                                                        subtasksForCurrentTask = listOf()
                                                        askSubtask = null
                                                        currentTaskHours = ""
                                                        currentTaskMinutes = ""
                                                        currentTaskSeconds = ""
                                                        currentSubtaskName = ""
                                                        currentSubtaskHours = ""
                                                        currentSubtaskMinutes = ""
                                                        currentSubtaskSeconds = ""
                                                        showTaskDialog = false
                                                    }
                                                },
                                                isAddEnabled = currentTaskName.isNotBlank() && askSubtask != null && (
                                                    askSubtask == false || (askSubtask == true && subtasksForCurrentTask.isNotEmpty() && currentSubtaskName.isBlank() && currentSubtaskMinutes.isBlank() && currentSubtaskSeconds.isBlank())
                                                ),
                                                askSubtask = askSubtask,
                                                onAskSubtaskChange = { askSubtask = it },
                                                onAddSubtask = {
                                                    if (currentSubtaskName.isNotBlank()) {
                                                        subtasksForCurrentTask = subtasksForCurrentTask + Subtask(currentSubtaskName, currentSubtaskHours, currentSubtaskMinutes, currentSubtaskSeconds)
                                                        currentSubtaskName = ""
                                                        currentSubtaskHours = ""
                                                        currentSubtaskMinutes = ""
                                                        currentSubtaskSeconds = ""
                                                    }
                                                },
                                                subtaskName = currentSubtaskName,
                                                onSubtaskNameChange = { currentSubtaskName = it },
                                                subtaskHours = currentSubtaskHours,
                                                onSubtaskHoursChange = { currentSubtaskHours = it.filter { c -> c.isDigit() } },
                                                subtaskMinutes = currentSubtaskMinutes,
                                                onSubtaskMinutesChange = { currentSubtaskMinutes = it.filter { c -> c.isDigit() } },
                                                subtaskSeconds = currentSubtaskSeconds,
                                                onSubtaskSecondsChange = { currentSubtaskSeconds = it.filter { c -> c.isDigit() } },
                                                isAddSubtaskEnabled = currentSubtaskName.isNotBlank(),
                                                subtasksForCurrentTask = subtasksForCurrentTask,
                                                onRemoveSubtask = { idx ->
                                                    subtasksForCurrentTask = subtasksForCurrentTask.toMutableList().also { it.removeAt(idx) }
                                                },
                                            )
                                        }
                                    },
                                    confirmButton = {},
                                    dismissButton = {}
                                )
                            }
                        }
                    }
                    // Session Duration section below task section if "Yes" is selected
                    Spacer(Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Session Duration (optional)", style = MaterialTheme.typography.titleMedium)
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = sessionHours,
                                    onValueChange = { sessionHours = it.filter { c -> c.isDigit() } },
                                    label = { Text("HH") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = sessionMinutes,
                                    onValueChange = { sessionMinutes = it.filter { c -> c.isDigit() } },
                                    label = { Text("MM") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null,
                                    isError = sessionMinutes.isNotBlank() && (sessionMinutes.toIntOrNull() ?: 0) !in 0..59
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = sessionSeconds,
                                    onValueChange = { sessionSeconds = it.filter { c -> c.isDigit() } },
                                    label = { Text("SS") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    placeholder = null,
                                    isError = sessionSeconds.isNotBlank() && (sessionSeconds.toIntOrNull() ?: 0) !in 0..59
                                )
                            }
                            if (sessionMinutes.isNotBlank() && (sessionMinutes.toIntOrNull() ?: 0) !in 0..59) {
                                Text(
                                    text = "Minutes must be between 0 and 59",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                                )
                            }
                            if (sessionSeconds.isNotBlank() && (sessionSeconds.toIntOrNull() ?: 0) !in 0..59) {
                                Text(
                                    text = "Seconds must be between 0 and 59",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(32.dp))
                val isMinutesValid = sessionMinutes.isBlank() || (sessionMinutes.toIntOrNull() ?: 0) in 0..59
                val isSecondsValid = sessionSeconds.isBlank() || (sessionSeconds.toIntOrNull() ?: 0) in 0..59
                val isDurationValid = (addTasks == false && (sessionHours.isNotBlank() || sessionMinutes.isNotBlank() || sessionSeconds.isNotBlank())) || addTasks == true
                val isFormValid = sessionName.isNotEmpty() && addTasks != null && isMinutesValid && isSecondsValid && isDurationValid
                Button(
                    onClick = {
                        // Save session logic

                        // Calculate session duration in seconds
                        val sessionDurationSeconds = (sessionHours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                            (sessionMinutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                            (sessionSeconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                        // Compose PracticeSession, Task, Subtask objects from UI state
                        val session = com.abhyasa.data.entities.PracticeSession(
                            sessionId = 0,
                            name = sessionName,
                            isTimed = sessionDurationSeconds > 0,
                            totalDuration = if (sessionDurationSeconds > 0) sessionDurationSeconds else null
                        )
                        val tasksWithSubtasks = tasks.map { (taskName, subtasks, timerTriple) ->
                            val hasSubtasks = subtasks.isNotEmpty()
                            val taskDuration = if (hasSubtasks) {
                                subtasks.sumOf {
                                    (it.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                    (it.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                    (it.seconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                                }
                            }
                            else if (timerTriple != null) {
                                val (h, m, s) = timerTriple
                                (h.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                        (m.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                        (s.ifBlank { "0" }.toIntOrNull() ?: 0)
                            }else null
                            val task = com.abhyasa.data.entities.Task(
                                taskId = 0,
                                sessionId = 0, // Will be set in repository
                                text = taskName,
                                hasSubtasks = hasSubtasks,
                                taskDuration = taskDuration,
                                typeLabel = ""
                            )
                            val subtaskEntities = subtasks.map {
                                com.abhyasa.data.entities.Subtask(
                                    subtaskId = 0,
                                    taskId = 0, // Will be set in repository
                                    name = it.name,
                                    duration = ((it.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 + (it.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 + (it.seconds.ifBlank { "0" }.toIntOrNull() ?: 0))
                                )
                            }
                            task to subtaskEntities
                        }
                        viewModel.createSession(
                            session = session,
                            tasksWithSubtasks = tasksWithSubtasks,
                            onSuccess = { newSessionId ->
                                navController.navigate("session_detail/$newSessionId")
                            }
                        )
                    },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Session")
                }
            }
        }
    }
}
