package com.example.exampractisehelper.ui.screens.createSession.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.ui.screens.createSession.Subtask

import com.example.exampractisehelper.ui.screens.createSession.components.AddSubtaskComponent

@Composable
fun AddTaskComponent(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    taskHours: String,
    onTaskHoursChange: (String) -> Unit,
    taskMinutes: String,
    onTaskMinutesChange: (String) -> Unit,
    taskSeconds: String,
    onTaskSecondsChange: (String) -> Unit,
    onAddTask: () -> Unit,
    isAddEnabled: Boolean,
    askSubtask: Boolean?,
    onAskSubtaskChange: (Boolean) -> Unit,
    onAddSubtask: (() -> Unit)? = null,
    subtaskName: String = "",
    onSubtaskNameChange: ((String) -> Unit)? = null,
    subtaskHours: String = "",
    onSubtaskHoursChange: ((String) -> Unit)? = null,
    subtaskMinutes: String = "",
    onSubtaskMinutesChange: ((String) -> Unit)? = null,
    subtaskSeconds: String = "",
    onSubtaskSecondsChange: ((String) -> Unit)? = null,
    isAddSubtaskEnabled: Boolean = false,
    subtasksForCurrentTask: List<Subtask> = emptyList(),
    onRemoveSubtask: ((Int) -> Unit)? = null,
    onEditSubtask: ((Int, String, String, String, String) -> Unit)? = null,
    buttonText: String? = "Add Task", // New parameter for button text
    onCancel: (() -> Unit)? = null // New parameter for cancel action
) {
    var isEditingSubtask by remember { mutableStateOf(false) }

    // Validation for task timer fields
    val isTaskMinutesValid = taskMinutes.isBlank() || (taskMinutes.toIntOrNull() ?: 0) in 0..59
    val isTaskSecondsValid = taskSeconds.isBlank() || (taskSeconds.toIntOrNull() ?: 0) in 0..59
    val isTaskDurationValid = taskHours.isNotBlank() || taskMinutes.isNotBlank() || taskSeconds.isNotBlank()

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = taskName,
                onValueChange = onTaskNameChange,
                label = { Text("Task Name* (eg, Essay)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("Do you want to add subtasks to this task?", style = MaterialTheme.typography.titleSmall)
        Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = askSubtask == true,
                onClick = { onAskSubtaskChange(true) }
            )
            Text("Yes", Modifier.padding(start = 4.dp, end = 16.dp))
            RadioButton(
                selected = askSubtask == false,
                onClick = { onAskSubtaskChange(false) }
            )
            Text("No", Modifier.padding(start = 4.dp))
        }
        if (askSubtask == false) {
            Spacer(Modifier.height(8.dp))
            // Task timer label
            Text("Task Duration", style = MaterialTheme.typography.labelMedium)
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskHours,
                    onValueChange = onTaskHoursChange,
                    label = { Text("HH") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = taskMinutes,
                    onValueChange = onTaskMinutesChange,
                    label = { Text("MM") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    isError = !isTaskMinutesValid
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = taskSeconds,
                    onValueChange = onTaskSecondsChange,
                    label = { Text("SS") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    isError = !isTaskSecondsValid
                )
            }
            if (!isTaskMinutesValid && taskMinutes.isNotBlank()) {
                Text(
                    text = "Minutes must be between 0 and 59",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            if (!isTaskSecondsValid && taskSeconds.isNotBlank()) {
                Text(
                    text = "Seconds must be between 0 and 59",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        } else if (askSubtask == true && onAddSubtask != null && onSubtaskNameChange != null && onSubtaskHoursChange != null && onSubtaskMinutesChange != null && onSubtaskSecondsChange != null) {
            Spacer(Modifier.height(8.dp))
            Text(if (isEditingSubtask) "Edit Subtask" else "Add Subtask", style = MaterialTheme.typography.titleSmall)
            AddSubtaskComponent(
                subtaskName = subtaskName,
                subtaskHours = subtaskHours,
                subtaskMinutes = subtaskMinutes,
                subtaskSeconds = subtaskSeconds,
                onSubtaskNameChange = onSubtaskNameChange,
                onSubtaskHoursChange = onSubtaskHoursChange,
                onSubtaskMinutesChange = onSubtaskMinutesChange,
                onSubtaskSecondsChange = onSubtaskSecondsChange,
                onAddSubtask = {
                    onAddSubtask?.invoke()
                    if (isEditingSubtask) isEditingSubtask = false // Only reset after update
                },
                isAddEnabled = isAddSubtaskEnabled,
                buttonText = if (isEditingSubtask) "Update Subtask" else "Add Subtask"
            )
            Spacer(Modifier.height(8.dp))
            // Show list of already added subtasks
            if (subtasksForCurrentTask.isNotEmpty()) {
                Text("Added Subtasks:", style = MaterialTheme.typography.titleSmall)
                Column(Modifier.padding(start = 8.dp)) {
                    subtasksForCurrentTask.forEachIndexed { idx: Int, subtask: Subtask ->
                        val h = subtask.hours.ifBlank { "0" }.toIntOrNull() ?: 0
                        val m = subtask.minutes.ifBlank { "0" }.toIntOrNull() ?: 0
                        val s = subtask.seconds.ifBlank { "0" }.toIntOrNull() ?: 0
                        val subtaskDuration = String.format("%dh %dm %ds", h, m, s)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx + 1}. ${subtask.name} ($subtaskDuration)", Modifier.weight(1f))
                            IconButton(onClick = {
                                // Prefill the subtask fields for editing
                                onSubtaskNameChange?.invoke(subtask.name)
                                onSubtaskHoursChange?.invoke(subtask.hours)
                                onSubtaskMinutesChange?.invoke(subtask.minutes)
                                onSubtaskSecondsChange?.invoke(subtask.seconds)
                                isEditingSubtask = true
                                // Remove the subtask from the list so it can be re-added after editing
                                onRemoveSubtask?.invoke(idx)
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Subtask")
                            }
                            IconButton(onClick = { onRemoveSubtask?.invoke(idx) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Subtask")
                            }
                        }
                    }
                }
            }
        }
        // Add Task/Update Task and Cancel buttons at the bottom
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = onAddTask,
                enabled = isAddEnabled && (askSubtask == false).let {
                    if (it) isTaskDurationValid && isTaskMinutesValid && isTaskSecondsValid else true
                }
            ) {
                Text(buttonText ?: "Add Task")
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { onCancel?.invoke() }) {
                Text("Cancel")
            }
        }
    }
}
