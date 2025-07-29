package com.abhyasa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.abhyasa.ui.screens.createSession.CreateSessionScreen
import com.abhyasa.ui.screens.createexam.CreateExamScreen
import com.abhyasa.ui.screens.createexam.CreateExamViewModel
import com.abhyasa.ui.screens.createexam.CreateExamViewModelFactory

@Composable
fun AppNavigator(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { /* HomeScreen(...) */ }
        composable(Screen.Exams.route) { /* ExamsScreen(...) */ }
        composable(Screen.Sessions.route) { /* SessionsScreen(...) */ }
        composable(Screen.Settings.route) { /* SettingsScreen(...) */ }
        composable(Screen.Profile.route) { /* ProfileScreen(...) */ }
        composable("create_session") {
            CreateSessionScreen(navController)
        }
        composable(
            "create_exam/{examId}",
            arguments = listOf(
                navArgument("examId") {
                    type = NavType.IntType
                    nullable = true
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId")
            val context = androidx.compose.ui.platform.LocalContext.current
            val db = androidx.room.Room.databaseBuilder(
                context,
                com.abhyasa.data.database.PracticeDatabase::class.java,
                "practice_db"
            ).build()
            val repository = com.abhyasa.data.repository.ExamRepository(db.examDao(), db.examTypeDao())
            val factory = CreateExamViewModelFactory(repository)
            val viewModel: CreateExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
            CreateExamScreen(viewModel = viewModel, navController = navController, backStackEntry = backStackEntry)
        }
        composable("about") {
            com.abhyasa.ui.screens.about.AboutScreen()
        }
    }
}
