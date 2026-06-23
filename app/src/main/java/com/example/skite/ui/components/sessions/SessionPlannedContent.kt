package com.example.skite.ui.components.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.ui.common.listComponent.cardListSection
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.UiState

@Composable
fun SessionPlannedContent(
    uiState: UiState.Success,
    onStartSession: (Map<Int, SessionAttendance>) -> Unit,
    onUpdateAttendance: (Map<Int, SessionAttendance>) -> Unit,
    modifier: Modifier = Modifier
) {
    var isStudentListExpanded by rememberSaveable { mutableStateOf(true) }

    val localAttendances = rememberSaveable (uiState.groupStudents, uiState.attendances) {
        mutableStateMapOf<Int, SessionAttendance>().apply {
            uiState.groupStudents.forEach { student ->
                put(student.id, uiState.attendances[student.id]?.attendance ?: SessionAttendance.PRESENT)
            }
        }
    }

    val studentAttendanceTitle = stringResource(R.string.TBD, uiState.groupStudents.size)



    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SessionDetailCard(session = uiState.session, sessionType = uiState.sessionType) }

        item {
            Button(
                onClick = { onStartSession(localAttendances) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.TBD),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        cardListSection(
            sectionKey = "students",
            sectionTitle = studentAttendanceTitle,
            entities = uiState.groupStudents,
            isExpanded = isStudentListExpanded,
            onToggle = {
                @Suppress("AssignedValueIsNeverRead")
                isStudentListExpanded = !isStudentListExpanded
            }
        ) { student ->
            val currentAtt = uiState.attendances[student.id]?.attendance ?: SessionAttendance.PRESENT
            StudentAttendanceCard(
                student = student,
                currentAttendance = currentAtt,
                onAttendanceChanged = { onUpdateAttendance(localAttendances) }
            )
        }

    }
}
