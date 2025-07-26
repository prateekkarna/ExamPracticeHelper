package com.example.exampractisehelper.ui.screens.sessiondetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: PracticeSession?,
    tasks: List<Task>,
    subtasksMap: Map<Int, List<Subtask>>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRun: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session?.name ?: "Session Details") },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = onRun) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (session == null) {
                Text("Session not found.")
            } else {
                Text("Name: ${session.name}")
                Spacer(Modifier.height(8.dp))
                Text("Timed: ${if (session.isTimed) "Yes" else "No"}")
                if (session.isTimed) {
                    Text("Duration: ${session.totalDuration ?: 0} min")
                }
                Spacer(Modifier.height(8.dp))
                Text("Loop: ${if (session.loopEnabled) "Yes (${session.loopCount})" else "No"}")
                Spacer(Modifier.height(8.dp))
                Text("Simple Session: ${if (session.isSimpleSession) "Yes" else "No"}")
                Spacer(Modifier.height(16.dp))
                Text("Tasks:", style = MaterialTheme.typography.titleMedium)
                if (tasks.isEmpty()) {
                    Text("No tasks found.")
                } else {
                    tasks.forEach { task ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Task: ${task.text}")
                                Text("Type: ${task.typeLabel}")
                                if (task.taskDuration != null) {
                                    Text("Duration: ${task.taskDuration} min")
                                }
                                if (task.hasSubtasks) {
                                    val subtasks = subtasksMap[task.taskId] ?: emptyList()
                                    if (subtasks.isNotEmpty()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text("Subtasks:", style = MaterialTheme.typography.bodyMedium)
                                        subtasks.forEach { subtask ->
                                            Text("- ${subtask.name} (${subtask.duration} min)")
                                        }
                                    } else {
                                        Text("No subtasks.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
