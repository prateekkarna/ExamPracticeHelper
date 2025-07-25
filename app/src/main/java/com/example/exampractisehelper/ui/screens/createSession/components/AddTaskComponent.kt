package com.example.exampractisehelper.ui.screens.createSession.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
) {
    Column {
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
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskHours,
                    onValueChange = onTaskHoursChange,
                    label = { Text("HH") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = taskMinutes,
                    onValueChange = onTaskMinutesChange,
                    label = { Text("MM") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = taskSeconds,
                    onValueChange = onTaskSecondsChange,
                    label = { Text("SS") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    placeholder = null
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onAddTask,
                    enabled = isAddEnabled
                ) {
                    Text("Add Task")
                }
            }
        } else if (askSubtask == true && onAddSubtask != null && onSubtaskNameChange != null && onSubtaskHoursChange != null && onSubtaskMinutesChange != null && onSubtaskSecondsChange != null) {
            Spacer(Modifier.height(8.dp))
            Text("Add Subtask", style = MaterialTheme.typography.titleSmall)
            AddSubtaskComponent(
                subtaskName = subtaskName,
                subtaskHours = subtaskHours,
                subtaskMinutes = subtaskMinutes,
                subtaskSeconds = subtaskSeconds,
                onSubtaskNameChange = onSubtaskNameChange,
                onSubtaskHoursChange = onSubtaskHoursChange,
                onSubtaskMinutesChange = onSubtaskMinutesChange,
                onSubtaskSecondsChange = onSubtaskSecondsChange,
                onAddSubtask = onAddSubtask,
                isAddEnabled = isAddSubtaskEnabled
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onAddTask,
                enabled = isAddEnabled
            ) {
                Text("Add Task")
            }
        }
    }
}
