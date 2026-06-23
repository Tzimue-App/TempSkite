package com.example.skite.ui.screens.sessionScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.entities.student.Student
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.UiState
import java.util.Date

@Preview(showBackground = true)
@Composable
private fun SessionDetailScreenPlannedPreview() {
    MaterialTheme {
        SessionDetailScreenContent(
            uiState = UiState.Success(
                session = Session(
                    id = 1,
                    name = "Swim Meet",
                    date = Date(),
                    groupId = 1,
                    state = SessionState.PLANNED
                ),
                sessionType = SessionType(
                    1,
                    "Chrono Mode",
                    1,
                    SessionTool.TIMER
                ),
                resultType = null,
                groupStudents = listOf(
                    Student(id = 1, name = "John Doe", number = 1, groupId = 1),
                    Student(id = 2, name = "Jane Smith", number = 2, groupId = 1)
                ),
                presentStudents = listOf(
                    Student(id = 1, name = "John Doe", number = 1, groupId = 1),
                    Student(id = 2, name = "Jane Smith", number = 2, groupId = 1)
                ),
                attendances = emptyMap(),
                results = emptyMap(),
                currentGradeDisplay = 100
            ),
            openDrawer = {},
            onNavigateBack = {},
            onStartSession = {},
            onFinishSession = {},
            onUpdateAttendance = { _ -> },
            onUpdateResultJson = { _, _ -> },
            onOverrideGrade = { _, _ -> },
            onGradeDisplayChanged = {},
            onRetry = {}
        )
    }
}
