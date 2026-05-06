package com.example.skite.ui.screens.sessionScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.entities.student.Student
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.SessionDetailUiState
import java.util.Date

@Preview(showBackground = true)
@Composable
private fun SessionDetailScreenPlannedPreview() {
    MaterialTheme {
        SessionDetailScreenContent(
            uiState = SessionDetailUiState.Success(
                session = Session(
                    id = 1,
                    name = "Swim Meet",
                    date = Date(),
                    groupId = 1,
                    state = SessionState.PLANNED
                ),
                sessionType = SessionType(1, "Chrono Mode", 1, "chono"),
                students = listOf(
                    Student(id = 1, name = "John Doe", number = 1, groupId = 1),
                    Student(id = 2, name = "Jane Smith", number = 2, groupId = 1)
                ),
                attendances = emptyMap(),
                results = emptyMap()
            ),
            openDrawer = {},
            onNavigateBack = {},
            onStartSession = {},
            onFinishSession = {},
            onUpdateAttendance = { _, _, _ -> },
            onUpdateResultJson = { _, _, _ -> },
            onOverrideGrade = { _, _, _ -> },
            onToggleStudents = {},
            onRetry = {}
        )
    }
}
