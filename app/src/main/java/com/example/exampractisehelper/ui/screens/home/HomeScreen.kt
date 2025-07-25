package com.example.exampractisehelper.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.exampractisehelper.data.database.PracticeDatabase
import com.example.exampractisehelper.data.repository.ExamRepository
import com.example.exampractisehelper.ui.components.ExamCard
import com.example.exampractisehelper.ui.screens.home.HomeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@Composable
fun HomeScreen(
    navController: NavController,
    onCreateExamClick: () -> Unit
) {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        PracticeDatabase::class.java,
        "practice_db"
    ).build()
    val repository = ExamRepository(db.examDao(), db.examTypeDao())
    val viewModel: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
    })
    val exams by viewModel.exams.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExams()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateExamClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Session"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                //.padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Sessions") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(exams.filter { it.name.contains(searchQuery, ignoreCase = true) }) { exam ->
                    ExamCard(
                        exam = exam,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("exam_detail/${exam.examId}")
                            }
                    )
                }
            }
            //Text("Hello")
        }
    }
}
