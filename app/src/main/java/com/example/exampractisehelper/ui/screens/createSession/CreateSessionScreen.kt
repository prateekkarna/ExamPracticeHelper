package com.example.exampractisehelper.ui.screens.createSession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.exampractisehelper.ui.screens.createSession.components.AddTaskComponent
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exampractisehelper.viewmodel.CreateSessionViewModel

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
    var tasks by remember { mutableStateOf(listOf<Pair<String, List<Subtask>>>()) }
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
    var editingTaskBackup by remember { mutableStateOf<Pair<String, List<Subtask>>?>(null) }
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Create Practice Session",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = sessionName,
                onValueChange = { sessionName = it },
                label = { Text("Session Name*") },
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
            // Session Duration is now always visible
            Text("Session Duration" + if (addTasks == true) " (optional)" else "*")
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = sessionHours,
                    onValueChange = { sessionHours = it.filter { c -> c.isDigit() } },
                    label = { Text("HH") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null,
                    isError = addTasks == false && sessionHours.isBlank() && sessionMinutes.isBlank() && sessionSeconds.isBlank()
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = sessionMinutes,
                    onValueChange = { sessionMinutes = it.filter { c -> c.isDigit() } },
                    label = { Text("MM") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null,
                    isError = addTasks == false && sessionHours.isBlank() && sessionMinutes.isBlank() && sessionSeconds.isBlank()
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = sessionSeconds,
                    onValueChange = { sessionSeconds = it.filter { c -> c.isDigit() } },
                    label = { Text("SS") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null,
                    isError = addTasks == false && sessionHours.isBlank() && sessionMinutes.isBlank() && sessionSeconds.isBlank()
                )
            }
            if (addTasks == false && sessionHours.isBlank() && sessionMinutes.isBlank() && sessionSeconds.isBlank()) {
                Text(
                    text = "Session duration is required when no tasks are added",
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
            if (addTasks == false) {
                Spacer(Modifier.height(16.dp))
                Text("Session Duration*")
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
                        placeholder = null
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = sessionSeconds,
                        onValueChange = { sessionSeconds = it.filter { c -> c.isDigit() } },
                        label = { Text("SS") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        placeholder = null
                    )
                }
            } else if (addTasks == true) {
                Spacer(Modifier.height(16.dp))
                Text("Add Tasks to Session", style = MaterialTheme.typography.titleMedium)
                Button(onClick = {
                    showTaskDialog = true
                    isEditingTask = false
                }) {
                    Text("Add Task")
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
                        itemsIndexed(tasks) { idx, (task, subtasks) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${idx + 1}. $task", Modifier.weight(1f))
                                        IconButton(onClick = {
                                            // Edit task: prefill fields and open dialog
                                            currentTaskName = task
                                            askSubtask = if (subtasks.isNotEmpty()) true else false
                                            subtasksForCurrentTask = subtasks
                                            showTaskDialog = true
                                            isEditingTask = true
                                            editingTaskIndex = idx
                                            editingTaskBackup = task to subtasks
                                            // Optionally clear subtask fields
                                            currentTaskHours = ""
                                            currentTaskMinutes = ""
                                            currentTaskSeconds = ""
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
                                    if (subtasks.isNotEmpty()) {
                                        Column(Modifier.padding(start = 24.dp)) {
                                            subtasks.forEachIndexed { sIdx, subtask ->
                                                Text("- ${subtask.name} (${subtask.hours}h ${subtask.minutes}m ${subtask.seconds}s)")
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
                                            val taskDuration = if (askSubtask == true) null else Triple(currentTaskHours, currentTaskMinutes, currentTaskSeconds)
                                            if (isEditingTask && editingTaskIndex >= 0) {
                                                tasks = tasks.toMutableList().apply { add(editingTaskIndex, currentTaskName to subtasksForCurrentTask) }
                                            } else {
                                                tasks = tasks + (currentTaskName to subtasksForCurrentTask)
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
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    // Save session logic
                    // Compose PracticeSession, Task, Subtask objects from UI state
                    // For demonstration, only sessionName is used
                    val totalDuration = if (
                        sessionHours.isNotBlank() || sessionMinutes.isNotBlank() || sessionSeconds.isNotBlank()
                    ) {
                        (sessionHours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                        (sessionMinutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                        (sessionSeconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                    } else null
                    val session = com.example.exampractisehelper.data.entities.PracticeSession(
                        sessionId = 0,
                        name = sessionName,
                        isTimed = addTasks == false,
                        totalDuration = totalDuration
                    )
                    val tasksWithSubtasks = tasks.map { (taskName, subtasks) ->
                        val task = com.example.exampractisehelper.data.entities.Task(
                            taskId = 0,
                            sessionId = 0, // Will be set in repository
                            text = taskName,
                            hasSubtasks = subtasks.isNotEmpty(),
                            taskDuration = if (subtasks.isEmpty()) null else null, // You can calculate if needed
                            typeLabel = ""
                        )
                        val subtaskEntities = subtasks.map {
                            com.example.exampractisehelper.data.entities.Subtask(
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
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                },
                enabled = sessionName.isNotEmpty() && addTasks != null && (addTasks == true || (sessionHours.isNotBlank() || sessionMinutes.isNotBlank() || sessionSeconds.isNotBlank())),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Session")
            }
        }
    }
}
