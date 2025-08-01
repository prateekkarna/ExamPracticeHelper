package com.abhyasa.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhyasa.data.database.PracticeDatabase
import com.abhyasa.data.database.MIGRATION_2_3
import com.abhyasa.data.repository.ExamRepository
import com.abhyasa.data.repository.PracticeSessionRepositoryImpl
import com.abhyasa.ui.components.ExamCard
import com.abhyasa.ui.components.PracticeSessionCard
import com.abhyasa.ui.screens.home.HomeViewModel
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment

@Composable
fun HomeScreen(
    navController: NavController,
    onCreateExamClick: () -> Unit
) {
    val context = LocalContext.current
    val db = PracticeDatabase.getInstance(context)
    val repository = ExamRepository(db.examDao(), db.examTypeDao())
    val sessionRepository = PracticeSessionRepositoryImpl(db.practiceSessionDao())
    val viewModel: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, sessionRepository) as T
        }
    })
    val exams by viewModel.exams.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sessions.filter { it.name.contains(searchQuery, ignoreCase = true) && it.sessionId > 0 }) { session ->
                        PracticeSessionCard(
                            session = session,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    if (session.sessionId > 0) {
                                        navController.navigate("session_detail/${session.sessionId}")
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}
