package com.example.exampractisehelper.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.repository.ExamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry

class CreateExamViewModel(private val repository: ExamRepository) : ViewModel() {
    fun saveExam(examName: String, examTypes: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val exam = Exam(name = examName, description = "", isCustom = true)
            val examId = repository.insertExam(exam)
            examTypes.forEach { typeName ->
                val examType = ExamType(examId = examId.toInt(), name = typeName, defaultDuration = null, userOverrideDuration = null)
                repository.insertExamType(examType)
            }
        }
    }

    suspend fun getExamById(examId: Int): Exam? {
        return repository.getExamById(examId)
    }

    suspend fun getExamTypesForExam(examId: Int): List<ExamType> {
        return repository.getExamTypesForExam(examId)
    }

    fun updateExam(examId: Int, examName: String, examTypes: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateExamName(examId, examName)
            repository.deleteExamTypesForExam(examId)
            examTypes.forEach { typeName ->
                val examType = ExamType(examId = examId, name = typeName, defaultDuration = null, userOverrideDuration = null)
                repository.insertExamType(examType)
            }
        }
    }
}

@Composable
fun CreateExamScreen(
    viewModel: CreateExamViewModel = viewModel(),
    navController: NavController,
    backStackEntry: NavBackStackEntry? = null
) {
    var examName by remember { mutableStateOf("") }
    var examTypes by remember { mutableStateOf(listOf<String>()) }
    var currentType by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("") }
    var editingTypeIndex by remember { mutableStateOf(-1) }
    var editingTypeName by remember { mutableStateOf("") }
    val examId: Int? = backStackEntry?.arguments?.getInt("examId")
    // Fallback: parse examId from navController's currentBackStackEntry if not found in arguments
    val route = navController.currentBackStackEntry?.destination?.route
    val parsedExamId: Int? = examId ?: route?.substringAfter("examId=")?.toIntOrNull()
    LaunchedEffect(parsedExamId) {
        println("[CreateExamScreen] examId from arguments: $examId")
        println("[CreateExamScreen] route: $route")
        println("[CreateExamScreen] parsedExamId: $parsedExamId")
        if (parsedExamId != null) {
            val exam = viewModel.getExamById(parsedExamId)
            println("[CreateExamScreen] loaded exam: $exam")
            examName = exam?.name ?: ""
            val types = viewModel.getExamTypesForExam(parsedExamId)
            println("[CreateExamScreen] loaded examTypes: $types")
            examTypes = types.map { it.name }
        }
    }
    val isExamNameValid = examName.isNotBlank()
    val scope = rememberCoroutineScope()


    Column(modifier = Modifier.padding(16.dp)) {
        Text(if (parsedExamId != null) "Edit Exam" else "Create a New Exam", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("You must provide a name for your exam. e.g. UPSC", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = examName,
            onValueChange = { examName = it },
            label = { Text("Exam Name*") },
            isError = !isExamNameValid && examName.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        if (!isExamNameValid && examName.isNotEmpty()) {
            Text("Exam name is required.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))
        Text("Exam Types (e.g. Mains, Prelims)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = currentType,
                onValueChange = { currentType = it },
                label = { Text("Add Exam Type") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (currentType.isNotBlank()) {
                        examTypes = examTypes + currentType
                        currentType = ""
                        infoMessage = "Exam type added."
                    } else {
                        infoMessage = "Type name cannot be empty."
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Type", modifier = Modifier.size(36.dp))
            }
        }
        if (infoMessage.isNotEmpty()) {
            Text(infoMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        examTypes.forEachIndexed { idx, type ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                if (editingTypeIndex == idx) {
                    OutlinedTextField(
                        value = editingTypeName,
                        onValueChange = { editingTypeName = it },
                        label = { Text("Edit Type Name") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (editingTypeName.isNotBlank()) {
                            examTypes = examTypes.mapIndexed { i, t -> if (i == idx) editingTypeName else t }
                            editingTypeIndex = -1
                            editingTypeName = ""
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Save Type Name")
                    }
                } else {
                    Text(type, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        editingTypeIndex = idx
                        editingTypeName = type
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Type Name")
                    }
                }
                IconButton(onClick = {
                    examTypes = examTypes.filterIndexed { i, _ -> i != idx }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Type")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (examId != null) {
                    scope.launch {
                        viewModel.updateExam(examId, examName, examTypes)
                        navController.popBackStack()
                    }
                } else {
                    viewModel.saveExam(examName, examTypes)
                    navController.popBackStack()
                }
            },
            enabled = isExamNameValid
        ) {
            Text(if (examId != null) "Update Exam" else "Create Exam")
        }
    }
}