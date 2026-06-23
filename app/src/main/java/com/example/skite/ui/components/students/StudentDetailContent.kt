package com.example.skite.ui.components.students

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skite.ui.common.listComponent.cardListSection
import com.example.skite.ui.components.groups.GroupDetailCard
import com.example.skite.ui.components.sessions.SessionCard
import com.example.skite.ui.viewModels.studentViewModels.StudentViewModel.UiState

@Composable
fun StudentDetailContent(
    uiState: UiState.Success,
    onSessionClick: (Int) -> Unit,
    onToggleSessions: () -> Unit,
    modifier: Modifier = Modifier
) {

    val groupsTitle = "Temp Need to be changed"
    val studentsTitle = "Temp need to be changed"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item(key = "student_detail_card") {
            StudentDetailCard(student = uiState.student)
            Spacer(modifier = Modifier.height(24.dp))
        }

        cardListSection(
            sectionKey = "group",
            sectionTitle = groupsTitle,
            entities = listOf(uiState.group),
            isExpanded = true,
            onToggle = {}
        ) { group ->
            GroupDetailCard(group = group)
        }

        item(key = "section_spacer") {
            Spacer(modifier = Modifier.height(24.dp))
        }

        cardListSection(
            sectionKey = "sessions",
            sectionTitle = studentsTitle,
            entities = uiState.sessionsWithAttendance,
            isExpanded = uiState.isSessionListExpanded,
            onToggle = onToggleSessions
        ) { sessionWithAttendance ->
            SessionCard(
                session = sessionWithAttendance.session,
                attendance = sessionWithAttendance.attendance,
                attendanceDisplay = true,
                onSessionClick = onSessionClick
            )
        }
    }
}
