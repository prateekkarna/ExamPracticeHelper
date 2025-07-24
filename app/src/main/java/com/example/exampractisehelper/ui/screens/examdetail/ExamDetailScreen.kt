package com.example.exampractisehelper.ui.screens.examdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.repository.ExamRepository
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ExamDetailScreen(
    examId: Int,
    repository: ExamRepository,
    navController: NavController
) {
    var exam by remember { mutableStateOf<Exam?>(null) }
    var examTypes by remember { mutableStateOf<List<ExamType>>(emptyList()) }
    var examName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(examId) {
        exam = repository.getExamById(examId)
        examTypes = repository.getExamTypesForExam(examId)
        examName = exam?.name ?: ""
    }

    Column(Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Exam Details", style = MaterialTheme.typography.headlineMedium)
            Row {
                IconButton(onClick = {
                    navController.navigate("create_exam/${exam!!.examId}")
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Exam")
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            exam?.let {
                                repository.deleteExam(it)
                                navController.popBackStack()
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Exam", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (exam != null) {
            if (isEditing) {
                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Exam Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    scope.launch {
                        repository.updateExam(exam!!.copy(name = examName))
                        isEditing = false
                        exam = repository.getExamById(examId)
                    }
                }) {
                    Text("Save")
                }
            } else {
                Text("Name: ${exam!!.name}", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
            Text("Exam Types", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            examTypes.forEach { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(type.name, style = MaterialTheme.typography.bodyLarge)
                        type.defaultDuration?.let {
                            Text("Default Duration: $it sec", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        } else {
            Text("Loading exam details...")
        }
    }
}
