package com.example.exampractisehelper.ui.screens.sessiondetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    session: PracticeSession?,
    tasks: List<Task>,
    subtasksMap: Map<Int, List<Subtask>>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRun: () -> Unit,
    navController: NavController,
    sessionRepository: com.example.exampractisehelper.data.repository.PracticeSessionRepository
) {
    var deleteRequested by remember { mutableStateOf(false) }
    LaunchedEffect(deleteRequested) {
        if (deleteRequested && session != null) {
            sessionRepository.deleteSessionById(session.sessionId)
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onRun, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { deleteRequested = true }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(24.dp))
                        }
                    }
                },
                actions = {},
                windowInsets = WindowInsets(0)
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            if (session == null) {
                Text("Session not found.")
            } else {
                // Session summary card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        // Remove extra space above session name
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(session.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                            val durationSecs = session.totalDuration ?: 0
                            val h = durationSecs / 3600
                            val m = (durationSecs % 3600) / 60
                            val s = durationSecs % 60
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = "Session Duration", tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (durationSecs > 0) String.format("%dh %dm %ds", h, m, s) else "No timer",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        if (session.loopEnabled) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(Modifier.width(4.dp))
                                Text("Loops: ${session.loopCount}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        if (session.isSimpleSession == true) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(4.dp))
                                Text("Simple Session", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
                // Tasks section
                Text("Tasks", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                if (tasks.isEmpty()) {
                    Text("No tasks found.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    tasks.forEach { task ->
                        var subtasksExpanded by remember { mutableStateOf(false) }
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.EventNote, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(Modifier.width(8.dp))
                                    Text(task.text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                                    val duration = task.taskDuration ?: 0
                                    val th = duration / 3600
                                    val tm = (duration % 3600) / 60
                                    val ts = duration % 60
                                    Text(
                                        text = if (duration > 0) String.format("%dh %dm %ds", th, tm, ts) else "No timer",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                if (task.hasSubtasks) {
                                    val subtasks = subtasksMap[task.taskId] ?: emptyList()
                                    if (subtasks.isNotEmpty()) {
                                        Spacer(Modifier.height(8.dp))
                                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { subtasksExpanded = !subtasksExpanded }) {
                                            Text("Subtasks", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = if (subtasksExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = if (subtasksExpanded) "Collapse" else "Expand"
                                            )
                                        }
                                        if (subtasksExpanded) {
                                            subtasks.forEach { subtask ->
                                                val sh = (subtask.duration ?: 0) / 3600
                                                val sm = ((subtask.duration ?: 0) % 3600) / 60
                                                val ss = (subtask.duration ?: 0) % 60
                                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(subtask.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                                    Text("${sh}h ${sm}m ${ss}s", style = MaterialTheme.typography.bodySmall)
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
        }
    }
}
