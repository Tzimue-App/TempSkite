package com.example.skite.ui.navigation

sealed class Screen(val route: String) {
    object Index       : Screen("index")
    object GroupList   : Screen("group_list")
    object SessionList : Screen("session_list")
    object StudentList : Screen("student_list")

    object Group : Screen("group/{groupId}") {
        fun createRoute(groupId: Int) = "group/$groupId"
    }

    object Session : Screen("session/{sessionId}") {
        fun createRoute(sessionId: Int) = "session/$sessionId"
    }

    object Student : Screen("student/{studentId}") {
        fun createRoute(studentId: Int) = "student/$studentId"
    }

    object Settings : Screen("settings")

    object SettingsSessionResult: Screen("settings/session_result")

    object SettingsLanguage: Screen("settings/language")

    object SettingsCalender: Screen("settings/calendar")
}