package com.example.skite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skite.ui.screens.HomeScreen
import com.example.skite.ui.screens.groupScreen.GroupDetailScreen
import com.example.skite.ui.screens.groupScreen.GroupListScreen
import com.example.skite.ui.screens.sessionScreen.SessionListScreen
import com.example.skite.ui.screens.sessionScreen.SessionDetailScreen
import com.example.skite.ui.screens.settingsScreen.CalendarScreen
import com.example.skite.ui.screens.settingsScreen.LanguageScreen
import com.example.skite.ui.screens.settingsScreen.SessionResultScreen
import com.example.skite.ui.screens.studentScreen.StudentListScreen
import com.example.skite.ui.screens.studentScreen.StudentDetailScreen
import com.example.skite.ui.screens.settingsScreen.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    openDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Index.route
    ) {

        composable(Screen.Index.route) {
            HomeScreen(openDrawer = openDrawer)
        }

        composable(Screen.GroupList.route) {
            GroupListScreen(
                openDrawer = openDrawer,
                onGroupClick = { groupId ->
                    navController.navigate(Screen.Group.createRoute(groupId))
                }
            )
        }

        composable(
            route = Screen.Group.route,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) {
            GroupDetailScreen(
                openDrawer = openDrawer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { sessionId ->
                    navController.navigate(Screen.Session.createRoute(sessionId))
                },
                onNavigateToStudent = { studentId ->
                    navController.navigate(Screen.Student.createRoute(studentId))
                }
            )
        }

        composable(Screen.SessionList.route) {
            SessionListScreen(
                openDrawer = openDrawer,
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.Session.createRoute(sessionId))
                }
            )
        }

        composable(
            route = Screen.Session.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) {
            SessionDetailScreen(
                openDrawer = openDrawer,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentList.route) {
            StudentListScreen(
                openDrawer = openDrawer,
                onStudentClick = { studentId ->
                    navController.navigate(Screen.Student.createRoute(studentId))
                }
            )
        }

        composable(
            route = Screen.Student.route,
            arguments = listOf(navArgument("studentId") { type = NavType.IntType })
        ) {
            StudentDetailScreen(
                openDrawer = openDrawer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { sessionId ->
                    navController.navigate(Screen.Session.createRoute(sessionId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                openDrawer = openDrawer,
                onCategoryClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.SettingsSessionResult.route) {
            SessionResultScreen(
                openDrawer = openDrawer
            )
        }

        composable(Screen.SettingsLanguage.route) {
            LanguageScreen(
                openDrawer = openDrawer
            )
        }

        composable(Screen.SettingsCalender.route) {
            CalendarScreen(
            )
        }
    }
}