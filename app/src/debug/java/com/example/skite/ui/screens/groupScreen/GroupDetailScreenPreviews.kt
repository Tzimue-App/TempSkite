package com.example.skite.ui.screens.groupScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.student.Student
import com.example.skite.data.entities.session.Session
import com.example.skite.ui.viewModels.groupViewModels.GroupViewModel.UiState
import java.util.Date

@Preview(showBackground = true)
@Composable
private fun GroupDetailScreenContentSuccessPreview() {
    MaterialTheme {
        GroupDetailScreenContent(
            uiState = UiState.Success(
                group = Group(id = 1, name = "Lundi 1-2", desc = "Cours du Lundi 1e é 2eme heure", year = 6),
                students = listOf(
                    Student(id = 2, name = "Alice", number = 1, groupId = 1),
                    Student(id = 3, name = "Bob", number = 2, groupId = 1)
                ),
                sessions = listOf(
                    Session(id = 4, name = "Course 1", date = Date(), groupId = 1, state = SessionState.PLANNED)
                ),
                isStudentListExpanded = true,
                isSessionListExpanded = true
            ),
            openDrawer = {},
            onToggleStudents = {},
            onToggleSessions = {},
            onAddStudents = {},
            onSessionClick = {},
            onStudentClick = {},
            onNavigateBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupDetailScreenContentLoadingPreview() {
    MaterialTheme {
        GroupDetailScreenContent(
            uiState = UiState.Loading,
            openDrawer = {},
            onToggleStudents = {},
            onToggleSessions = {},
            onAddStudents = {},
            onSessionClick = {},
            onStudentClick = {},
            onNavigateBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupDetailScreenContentEmptyPreview() {
    MaterialTheme {
        GroupDetailScreenContent(
            uiState = UiState.Empty,
            openDrawer = {},
            onToggleStudents = {},
            onToggleSessions = {},
            onAddStudents = {},
            onSessionClick = {},
            onStudentClick = {},
            onNavigateBack = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupDetailScreenContentErrorPreview() {
    MaterialTheme {
        GroupDetailScreenContent(
            uiState = UiState.Error(null),
            openDrawer = {},
            onToggleStudents = {},
            onToggleSessions = {},
            onAddStudents = {},
            onSessionClick = {},
            onStudentClick = {},
            onNavigateBack = {},
            onRetry = {}
        )
    }
}
