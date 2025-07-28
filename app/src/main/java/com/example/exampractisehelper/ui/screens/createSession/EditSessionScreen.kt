package com.example.exampractisehelper.ui.screens.createSession

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
import com.example.exampractisehelper.ui.screens.createSession.components.AddTaskComponent
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.exampractisehelper.viewmodel.CreateSessionViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.exampractisehelper.data.database.PracticeDatabase
import com.example.exampractisehelper.data.repository.PracticeSessionRepositoryImpl
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Use the UI Subtask type everywhere
import com.example.exampractisehelper.ui.screens.createSession.Subtask as UiSubtask

@Composable
fun EditSessionScreen(
    sessionId: Int,
    navController: NavController,
    viewModel: CreateSessionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            PracticeDatabase::class.java,
            "exam_practise_helper_db_v2"
        ).fallbackToDestructiveMigration()
         .fallbackToDestructiveMigrationOnDowngrade()
         .build()
    }
    val sessionRepository = remember { PracticeSessionRepositoryImpl(db.practiceSessionDao()) }
    val taskDao = remember { db.taskDao() }
    val subTaskDao = remember { db.subTaskDao() }
    val coroutineScope = rememberCoroutineScope()
    // State for loading
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var sessionName by remember { mutableStateOf("") }
    var addTasks by remember { mutableStateOf<Boolean?>(null) }
    var sessionHours by remember { mutableStateOf("") }
    var sessionMinutes by remember { mutableStateOf("") }
    var sessionSeconds by remember { mutableStateOf("") }
    var tasks by remember { mutableStateOf(listOf<Triple<String, List<UiSubtask>, Triple<String, String, String>?>>()) }
    var subtasksForCurrentTask by remember { mutableStateOf(listOf<UiSubtask>()) }
    var currentTaskName by remember { mutableStateOf("") }
    var currentTaskHours by remember { mutableStateOf("") }
    var currentTaskMinutes by remember { mutableStateOf("") }
    var currentTaskSeconds by remember { mutableStateOf("") }
    var currentSubtaskName by remember { mutableStateOf("") }
    var currentSubtaskHours by remember { mutableStateOf("") }
    var currentSubtaskMinutes by remember { mutableStateOf("") }
    var currentSubtaskSeconds by remember { mutableStateOf("") }
    var askSubtask by remember { mutableStateOf<Boolean?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var isEditingTask by remember { mutableStateOf(false) }
    var editingTaskIndex by remember { mutableStateOf(-1) }
    var editingTaskBackup by remember { mutableStateOf<Triple<String, List<UiSubtask>, Triple<String, String, String>?>?>(null) }
    // Load session data on first composition
    LaunchedEffect(sessionId) {
        try {
            val session = sessionRepository.getAllSessions().find { it.sessionId == sessionId }
            if (session == null) {
                loadError = "Session not found"
                isLoading = false
                return@LaunchedEffect
            }
            sessionName = session.name
            val totalDuration = session.totalDuration ?: 0
            sessionHours = (totalDuration / 3600).toString().takeIf { totalDuration > 0 } ?: ""
            sessionMinutes = ((totalDuration % 3600) / 60).toString().takeIf { totalDuration > 0 } ?: ""
            sessionSeconds = (totalDuration % 60).toString().takeIf { totalDuration > 0 } ?: ""
            val loadedTasks = taskDao.getTasksForSession(sessionId)
            addTasks = loadedTasks.isNotEmpty() // Set radio to Yes if tasks exist
            val loaded = loadedTasks.map { task ->
                val subtasks = subTaskDao.getSubtasksForTask(task.taskId)
                if (subtasks.isNotEmpty()) {
                    Triple(
                        task.text,
                        subtasks.map {
                            UiSubtask(
                                name = it.name,
                                hours = (it.duration / 3600).toString(),
                                minutes = ((it.duration % 3600) / 60).toString(),
                                seconds = (it.duration % 60).toString()
                            )
                        },
                        null
                    )
                } else {
                    val dur = task.taskDuration ?: 0
                    Triple(
                        task.text,
                        listOf(),
                        Triple(
                            (dur / 3600).toString().takeIf { dur > 0 } ?: "",
                            ((dur % 3600) / 60).toString().takeIf { dur > 0 } ?: "",
                            (dur % 60).toString().takeIf { dur > 0 } ?: ""
                        )
                    )
                }
            }
            tasks = loaded
            isLoading = false
        } catch (e: Exception) {
            loadError = e.message
            isLoading = false
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (loadError != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $loadError")
        }
        return
    }

    // ...existing code from CreateSessionScreen, but change the title and button to 'Edit Session' and 'Update Session'...
    // ...existing code...
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
                            text = "Edit Practice Session",
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
                                Text("Edit Tasks in Session", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.weight(1f))
                                Button(onClick = {
                                    showTaskDialog = true
                                    isEditingTask = false
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
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 250.dp)
                                ) {
                                    itemsIndexed(tasks) { idx, (taskName, subtasks, timerTriple) ->
                                        val hasSubtasks = subtasks.isNotEmpty()
                                        // Fix sumOf and property access by specifying parameter type
                                        val totalTaskSeconds = if (hasSubtasks) subtasks.sumOf { sub: UiSubtask ->
                                            (sub.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                            (sub.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                            (sub.seconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                                        } else timerTriple?.let { (h, m, s) ->
                                            (h.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                            (m.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                            (s.ifBlank { "0" }.toIntOrNull() ?: 0)
                                        }
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
                                                            // Also fix subtask duration formatting
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
                                                        subtasksForCurrentTask = subtasksForCurrentTask + UiSubtask(
                                                            name = currentSubtaskName,
                                                            hours = currentSubtaskHours,
                                                            minutes = currentSubtaskMinutes,
                                                            seconds = currentSubtaskSeconds
                                                        )
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
                        // Update session logic
                        val sessionDurationSeconds = (sessionHours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                            (sessionMinutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                            (sessionSeconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                        val session = PracticeSession(
                            sessionId = sessionId,
                            name = sessionName,
                            isTimed = sessionDurationSeconds > 0,
                            totalDuration = if (sessionDurationSeconds > 0) sessionDurationSeconds else null
                        )
                        val tasksWithSubtasks = tasks.map { (taskName, subtasks, timerTriple) ->
                            val hasSubtasks = subtasks.isNotEmpty()
                            val taskDuration = if (hasSubtasks) {
                                subtasks.sumOf { sub: UiSubtask ->
                                    (sub.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                    (sub.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                    (sub.seconds.ifBlank { "0" }.toIntOrNull() ?: 0)
                                }
                            } else if (timerTriple != null) {
                                val (h, m, s) = timerTriple
                                (h.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 +
                                        (m.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 +
                                        (s.ifBlank { "0" }.toIntOrNull() ?: 0)
                            } else null
                            val task = Task(
                                taskId = 0,
                                sessionId = sessionId,
                                text = taskName,
                                hasSubtasks = hasSubtasks,
                                taskDuration = taskDuration,
                                typeLabel = ""
                            )
                            val subtaskEntities = subtasks.map { sub: UiSubtask ->
                                com.example.exampractisehelper.data.entities.Subtask(
                                    subtaskId = 0,
                                    taskId = 0,
                                    name = sub.name,
                                    duration = ((sub.hours.ifBlank { "0" }.toIntOrNull() ?: 0) * 3600 + (sub.minutes.ifBlank { "0" }.toIntOrNull() ?: 0) * 60 + (sub.seconds.ifBlank { "0" }.toIntOrNull() ?: 0))
                                )
                            }
                            task to subtaskEntities
                        }
                        coroutineScope.launch {
                            // Delete old tasks/subtasks, then insert new ones
                            val oldTasks = taskDao.getTasksForSession(sessionId)
                            for (t in oldTasks) {
                                subTaskDao.deleteSubtasksByTaskId(t.taskId)
                                taskDao.deleteTaskById(t.taskId)
                            }
                            db.practiceSessionDao().updateSession(session.sessionId, session.name, session.isTimed, session.totalDuration)
                            tasksWithSubtasks.forEach { (task, subtasks) ->
                                val taskId = taskDao.insert(task.copy(sessionId = sessionId)).toInt()
                                subtasks.forEach { subtask ->
                                    subTaskDao.insert(subtask.copy(taskId = taskId))
                                }
                            }
                            withContext(Dispatchers.Main) {
                                navController.navigate("session_detail/$sessionId")
                            }
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Session")
                }
            }
        }
    }
}
