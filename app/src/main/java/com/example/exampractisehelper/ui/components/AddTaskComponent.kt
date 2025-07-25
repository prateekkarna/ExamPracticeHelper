package com.example.exampractisehelper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskComponent(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    onAddTask: () -> Unit,
    isAddEnabled: Boolean
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = taskName,
            onValueChange = onTaskNameChange,
            label = { Text("Task Name*") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onAddTask,
            enabled = isAddEnabled
        ) {
            Text("Add Task")
        }
    }
}

