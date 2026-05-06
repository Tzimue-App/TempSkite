package com.example.skite.ui.components.groups

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.ui.common.classComponent.TrailingAction
import com.example.skite.ui.common.listComponent.cardListSection
import com.example.skite.ui.components.sessions.SessionCard
import com.example.skite.ui.components.students.StudentCard
import com.example.skite.ui.viewModels.groupViewModels.GroupViewModel.UiState

@Composable
fun GroupDetailContent(
    uiState: UiState.Success,
    onToggleStudents: () -> Unit,
    onToggleSessions: () -> Unit,
    onAddStudentClick: () -> Unit,
    onSessionClick: (Int) -> Unit,
    onStudentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val studentsTitle = stringResource(
        R.string.group_detail_content_students_header_title,
        uiState.students.size
    )
    val sessionsTitle = stringResource(
        R.string.group_detail_content_sessions_header_title,
        uiState.sessions.size
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item(key = "group_card") {
            GroupDetailCard(group = uiState.group)
            Spacer(modifier = Modifier.height(24.dp))
        }

        cardListSection(
            sectionKey = "students",
            sectionTitle = studentsTitle,
            entities = uiState.students,
            isExpanded = uiState.isStudentListExpanded,
            onToggle = onToggleStudents,
            trailingAction = TrailingAction(
                icon = Icons.Default.Add,
                contentDescription = null,
                onClick = onAddStudentClick
            )
        ) { student ->
            StudentCard(
                student = student,
                onStudentClick = onStudentClick
            )
        }

        item(key = "section_spacer") {
            Spacer(modifier = Modifier.height(24.dp))
        }

        cardListSection(
            sectionKey = "sessions",
            sectionTitle = sessionsTitle,
            entities = uiState.sessions,
            isExpanded = uiState.isSessionListExpanded,
            onToggle = onToggleSessions
        ) { session ->
            SessionCard(
                session = session,
                onSessionClick = onSessionClick
            )
        }
    }
}