package com.example.data_manajemet.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object MyData : Screen("mydata/{userId}") {
        fun createRoute(userId: Int) = "mydata/$userId"
    }
    object Help : Screen("help")
    object Settings : Screen("settings")
}



