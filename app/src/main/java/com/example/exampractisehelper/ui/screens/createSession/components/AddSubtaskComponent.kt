package com.example.exampractisehelper.ui.screens.createSession.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddSubtaskComponent(
    subtaskName: String,
    subtaskHours: String,
    subtaskMinutes: String,
    subtaskSeconds: String,
    onSubtaskNameChange: (String) -> Unit,
    onSubtaskHoursChange: (String) -> Unit,
    onSubtaskMinutesChange: (String) -> Unit,
    onSubtaskSecondsChange: (String) -> Unit,
    onAddSubtask: () -> Unit,
    isAddEnabled: Boolean,
    buttonText: String = "Add Subtask"
) {
    val isMinutesValid = subtaskMinutes.isBlank() || (subtaskMinutes.toIntOrNull() ?: 0) in 0..59
    val isSecondsValid = subtaskSeconds.isBlank() || (subtaskSeconds.toIntOrNull() ?: 0) in 0..59
    val isDurationValid = subtaskHours.isNotBlank() || subtaskMinutes.isNotBlank() || subtaskSeconds.isNotBlank()

    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = subtaskName,
                onValueChange = onSubtaskNameChange,
                label = { Text("Subtask Name* (eg. Intro, Conclusion)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Subtask timer label
        Text("Subtask Duration", style = MaterialTheme.typography.labelMedium)
        // Subtask timer row
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = subtaskHours,
                onValueChange = onSubtaskHoursChange,
                label = { Text("HH") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = subtaskMinutes,
                onValueChange = onSubtaskMinutesChange,
                label = { Text("MM") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                isError = !isMinutesValid
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = subtaskSeconds,
                onValueChange = onSubtaskSecondsChange,
                label = { Text("SS") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                isError = !isSecondsValid
            )
        }
        if (!isMinutesValid && subtaskMinutes.isNotBlank()) {
            Text(
                text = "Minutes must be between 0 and 59",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        if (!isSecondsValid && subtaskSeconds.isNotBlank()) {
            Text(
                text = "Seconds must be between 0 and 59",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Add Subtask button row
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onAddSubtask,
                enabled = isAddEnabled && isDurationValid && isMinutesValid && isSecondsValid
            ) {
                Text(buttonText)
            }
        }
    }
}
