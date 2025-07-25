package com.example.exampractisehelper.ui.screens.createSession

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.exampractisehelper.ui.screens.createSession.components.AddTaskComponent

data class Subtask(val name: String, val hours: String, val minutes: String, val seconds: String)

@Composable
fun CreateSessionScreen(
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
                // Task input row
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
                    onAddTask = {
                        if (currentTaskName.isNotBlank()) {
                            val taskDuration = if (askSubtask == true) null else Triple(currentTaskHours, currentTaskMinutes, currentTaskSeconds)
                            tasks = tasks + (currentTaskName to subtasksForCurrentTask)
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
                    isAddSubtaskEnabled = currentSubtaskName.isNotBlank()
                )
                Spacer(Modifier.height(8.dp))
                if (tasks.isNotEmpty()) {
                    Text("Tasks:", style = MaterialTheme.typography.titleSmall)
                    tasks.forEachIndexed { idx, (task, subtasks) ->
                        Column(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${idx + 1}. $task", Modifier.weight(1f))
                                IconButton(onClick = {
                                    tasks = tasks.toMutableList().also { it.removeAt(idx) }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Task")
                                }
                            }
                            if (subtasks.isNotEmpty()) {
                                Column(Modifier.padding(start = 24.dp)) {
                                    subtasks.forEachIndexed { sIdx, subtask ->
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Text("- $subtask", Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { /* TODO: Save session logic */ },
                enabled = sessionName.isNotEmpty() && addTasks != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Session")
            }
        }
    }
}
