package com.abhyasa.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.room.Room
import com.abhyasa.data.database.PracticeDatabase
import com.abhyasa.data.repository.ExamRepository
import com.abhyasa.ui.components.AppScaffold
import com.abhyasa.ui.screens.createexam.CreateExamScreen
import com.abhyasa.ui.screens.createexam.CreateExamViewModelFactory
import com.abhyasa.ui.screens.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhyasa.ui.screens.createSession.CreateSessionScreen
import com.abhyasa.ui.screens.createexam.CreateExamViewModel

@Composable
fun AppRootNavigation() {
    val navController = rememberNavController()
    AppScaffold(
        navController = navController,
        drawerItems = listOf("Home", "Sessions" ,"Activity", "Settings", "About")
    ) {
        NavHost(navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController, onCreateExamClick = {
                    navController.navigate("create_session")
                })
            }
            composable("activity") {
                com.abhyasa.ui.screens.timer.TimerScreen()
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
                    com.abhyasa.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val repository = com.abhyasa.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
                com.abhyasa.ui.screens.examdetail.ExamDetailScreen(
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
                    com.abhyasa.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val repository = com.abhyasa.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
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
                    com.abhyasa.data.database.PracticeDatabase::class.java,
                    "exam_practise_helper_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                val sessionRepository = com.abhyasa.data.repository.PracticeSessionRepositoryImpl(db.practiceSessionDao())
                val taskDao = db.taskDao()
                val subTaskDao = db.subTaskDao()
                val (session, setSession) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.abhyasa.data.entities.PracticeSession?>(null) }
                val (tasks, setTasks) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<List<com.abhyasa.data.entities.Task>>(emptyList()) }
                val (subtasksMap, setSubtasksMap) = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Map<Int, List<com.abhyasa.data.entities.Subtask>>>(emptyMap()) }
                androidx.compose.runtime.LaunchedEffect(sessionId) {
                    setSession(sessionRepository.getAllSessions().find { it.sessionId == sessionId })
                    val loadedTasks = taskDao.getTasksForSession(sessionId)
                    setTasks(loadedTasks)
                    setSubtasksMap(loadedTasks.associate { it.taskId to subTaskDao.getSubtasksForTask(it.taskId) })
                }
                com.abhyasa.ui.screens.sessiondetail.SessionDetailScreen(
                    session = session,
                    tasks = tasks,
                    subtasksMap = subtasksMap,
                    onEdit = {
                        session?.let {
                            navController.navigate("edit_session/${it.sessionId}")
                        }
                    },
                    onDelete = {
                        // Just signal delete intent, actual deletion handled in SessionDetailScreen
                    },
                    onRun = {
                        session?.let {
                            navController.navigate("run_session/${it.sessionId}")
                        }
                    },
                    navController = navController,
                    sessionRepository = sessionRepository
                )
            }
            composable(
                route = "run_session/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                com.abhyasa.ui.screens.runsession.RunSessionScreen(
                    sessionId = sessionId,
                    navController = navController
                )
            }
            composable("profile") {
                com.abhyasa.ui.screens.profile.ProfileScreen()
            }
            composable("about") {
                com.abhyasa.ui.screens.about.AboutScreen()
            }
            composable(
                route = "edit_session/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
                com.abhyasa.ui.screens.createSession.EditSessionScreen(
                    sessionId = sessionId,
                    navController = navController
                )
            }
            composable("settings") {
                com.abhyasa.ui.screens.settings.SettingsScreen()
            }
        }
    }
}
