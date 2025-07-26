package com.example.exampractisehelper.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.room.Room
import com.example.exampractisehelper.data.database.PracticeDatabase
import com.example.exampractisehelper.data.repository.ExamRepository
import com.example.exampractisehelper.ui.components.AppScaffold
import com.example.exampractisehelper.ui.screens.createexam.CreateExamScreen
import com.example.exampractisehelper.ui.screens.createexam.CreateExamViewModelFactory
import com.example.exampractisehelper.ui.screens.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exampractisehelper.ui.screens.createSession.CreateSessionScreen
import com.example.exampractisehelper.ui.screens.createexam.CreateExamViewModel

@Composable
fun AppRootNavigation() {
    val navController = rememberNavController()
    AppScaffold(
        navController = navController,
        drawerItems = listOf("Home", "Exams", "Sessions", "Settings")
    ) {
        NavHost(navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController, onCreateExamClick = {
                    navController.navigate("create_session")
                })
                //Text("Home Screen")
            }
            composable("create_exam") {
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = Room.databaseBuilder(
                    context,
                    PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val repository = ExamRepository(db.examDao(), db.examTypeDao())
                val factory = CreateExamViewModelFactory(repository)
                val viewModel: CreateExamViewModel = viewModel(factory = factory)
                CreateExamScreen(viewModel = viewModel, navController = navController)
            }
            composable(
                route = "exam_detail/{examId}",
                arguments = listOf(
                    androidx.navigation.navArgument("examId") {
                        type = androidx.navigation.NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val examId = backStackEntry.arguments?.getInt("examId") ?: 0
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = androidx.room.Room.databaseBuilder(
                    context,
                    com.example.exampractisehelper.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val repository = com.example.exampractisehelper.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
                com.example.exampractisehelper.ui.screens.examdetail.ExamDetailScreen(
                    examId = examId,
                    repository = repository,
                    navController = navController
                )
            }
            composable(
                "create_exam/{examId}",
                arguments = listOf(
                    navArgument("examId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val examId = backStackEntry.arguments?.getInt("examId")
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = androidx.room.Room.databaseBuilder(
                    context,
                    com.example.exampractisehelper.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val repository = com.example.exampractisehelper.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
                val factory = CreateExamViewModelFactory(repository)
                val viewModel: CreateExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                CreateExamScreen(viewModel = viewModel, navController = navController, backStackEntry = backStackEntry)
            }
            composable("create_session") {
                CreateSessionScreen(navController)
            }
            composable(
                route = "session_detail/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = androidx.room.Room.databaseBuilder(
                    context,
                    com.example.exampractisehelper.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val sessionRepository = com.example.exampractisehelper.data.repository.PracticeSessionRepositoryImpl(db.practiceSessionDao())
                val taskDao = db.taskDao()
                val subTaskDao = db.subTaskDao()
                val (session, setSession) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.exampractisehelper.data.entities.PracticeSession?>(null) }
                val (tasks, setTasks) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<List<com.example.exampractisehelper.data.entities.Task>>(emptyList()) }
                val (subtasksMap, setSubtasksMap) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Map<Int, List<com.example.exampractisehelper.data.entities.Subtask>>>(emptyMap()) }
                androidx.compose.runtime.LaunchedEffect(sessionId) {
                    setSession(sessionRepository.getAllSessions().find { it.sessionId == sessionId })
                    val loadedTasks = taskDao.getTasksForSession(sessionId)
                    setTasks(loadedTasks)
                    setSubtasksMap(loadedTasks.associate { it.taskId to subTaskDao.getSubtasksForTask(it.taskId) })
                }
                com.example.exampractisehelper.ui.screens.sessiondetail.SessionDetailScreen(
                    session = session,
                    tasks = tasks,
                    subtasksMap = subtasksMap,
                    onEdit = { /* TODO: Implement edit */ },
                    onDelete = { /* TODO: Implement delete */ },
                    onRun = { /* TODO: Implement run */ }
                )
            }
        }
    }
}
