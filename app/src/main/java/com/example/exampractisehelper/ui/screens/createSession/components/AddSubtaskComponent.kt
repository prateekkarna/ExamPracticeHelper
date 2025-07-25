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
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = subtaskName,
                onValueChange = onSubtaskNameChange,
                label = { Text("Subtask Name") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
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
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = subtaskSeconds,
                onValueChange = onSubtaskSecondsChange,
                label = { Text("SS") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Add Subtask button row
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onAddSubtask,
                enabled = isAddEnabled
            ) {
                Text(buttonText)
            }
        }
    }
}
