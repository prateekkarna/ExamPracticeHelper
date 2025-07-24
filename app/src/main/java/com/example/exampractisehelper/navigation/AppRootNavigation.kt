package com.example.exampractisehelper.navigation

import android.content.Context
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
import com.example.exampractisehelper.ui.screens.home.CreateExamScreen
import com.example.exampractisehelper.ui.screens.home.CreateExamViewModelFactory
import com.example.exampractisehelper.ui.screens.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel

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
                    navController.navigate("create_exam")
                })
            }
            composable("create_exam") {
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = Room.databaseBuilder(
                    context,
                    PracticeDatabase::class.java,
                    "practice_db"
                ).build()
                val repository = ExamRepository(db.examDao(), db.examTypeDao())
                val factory = CreateExamViewModelFactory(repository)
                val viewModel: com.example.exampractisehelper.ui.screens.home.CreateExamViewModel = viewModel(factory = factory)
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
                    "practice_db"
                ).build()
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
                    "practice_db"
                ).build()
                val repository = com.example.exampractisehelper.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
                val factory = com.example.exampractisehelper.ui.screens.home.CreateExamViewModelFactory(repository)
                val viewModel: com.example.exampractisehelper.ui.screens.home.CreateExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                com.example.exampractisehelper.ui.screens.home.CreateExamScreen(viewModel = viewModel, navController = navController, backStackEntry = backStackEntry)
            }
        }
    }
}
