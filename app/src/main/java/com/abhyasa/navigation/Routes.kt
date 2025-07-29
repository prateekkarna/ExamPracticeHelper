package com.abhyasa.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Exams : Screen("exams")
    object Sessions : Screen("sessions")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}

