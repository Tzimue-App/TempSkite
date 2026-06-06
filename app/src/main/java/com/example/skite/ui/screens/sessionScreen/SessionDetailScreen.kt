package com.example.skite.ui.screens.sessionScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.common.stateComponent.NotFoundContent
import com.example.skite.ui.common.formComponent.SectionHeader
import com.example.skite.ui.components.sessions.ChronoEvaluationComponent
import com.example.skite.ui.components.sessions.DefaultEvaluationComponent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.UiState

@Composable
fun SessionDetailScreen(
    openDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SessionDetailScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onNavigateBack = onNavigateBack,
        onStartSession = viewModel::startSession,
        onFinishSession = viewModel::finishSession,
        onUpdateAttendance = { studentId, sessionId, attendance -> viewModel.updateAttendance(studentId, sessionId, attendance) },
        onUpdateResultJson = { studentId, sessionId, json -> viewModel.updateResultJson(studentId, sessionId, json) },
        onOverrideGrade = { studentId, sessionId, grade -> viewModel.overrideFinalGrade(studentId, sessionId, grade) },
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    onStartSession: () -> Unit,
    onFinishSession: () -> Unit,
    onUpdateAttendance: (Int, Int, SessionAttendance) -> Unit,
    onUpdateResultJson: (Int, Int, String) -> Unit,
    onOverrideGrade: (Int, Int, Float) -> Unit,
    onRetry: () -> Unit
) {
    WithDrawer(
        title = "Session Detail",
        openDrawer = openDrawer
    ) {
        when (val state = uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Empty -> NotFoundContent(
                message = "Session introuvable",
                onNavigateBack = onNavigateBack
            )
            is UiState.Error -> ErrorContent(
                message = state.message ?: "General Error",
                onRetry = onRetry
            )
            is UiState.Success -> {
                when (state.session.state) {
                    SessionState.PLANNED -> PlannedContent(
                        state = state,
                        onStartSession = onStartSession,
                        onUpdateAttendance = { studentId, att -> onUpdateAttendance(studentId, state.session.id, att) }
                    )
                    SessionState.IN_PROGRESS -> ProgressContent(
                        state = state,
                        onFinishSession = onFinishSession,
                        onUpdateResultJson = { studentId, json -> onUpdateResultJson(studentId, state.session.id, json) }
                    )
                    SessionState.FINISHED -> FinishedContent(
                        state = state,
                        onOverrideGrade = { studentId, grade -> onOverrideGrade(studentId, state.session.id, grade) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlannedContent(
    state: UiState.Success,
    onStartSession: () -> Unit,
    onUpdateAttendance: (Int, SessionAttendance) -> Unit
) {
    var isStudentListExpanded by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("State: PLANNED", style = MaterialTheme.typography.titleMedium)
        Text("SessionType Template: ${state.sessionType?.name ?: "No Template Attached"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartSession, modifier = Modifier.fillMaxWidth()) {
            Text("Start Session")
        }
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader("Students (${state.groupStudents.size})", isStudentListExpanded, { isStudentListExpanded = !isStudentListExpanded })

        if (isStudentListExpanded) {
            LazyColumn {
                items(state.groupStudents, key = { it.id }) { student ->
                    val currentAtt = state.attendances[student.id]?.attendance
                    Row(
                           modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                           horizontalArrangement = Arrangement.SpaceBetween,
                           verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(student.name)
                        Row {
                            TextButton(onClick = { onUpdateAttendance(student.id, SessionAttendance.PRESENT) }) {
                                Text(if (currentAtt == SessionAttendance.PRESENT) "[PRESENT]" else "Present")
                            }
                            TextButton(onClick = { onUpdateAttendance(student.id, SessionAttendance.MISSING) }) {
                                Text(if (currentAtt == SessionAttendance.MISSING) "[ABSENT]" else "Absent")
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ProgressContent(
    state: UiState.Success,
    onFinishSession: () -> Unit,
    onUpdateResultJson: (Int, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("State: IN PROGRESS", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onFinishSession, modifier = Modifier.fillMaxWidth()) {
            Text("Finish Session")
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.presentStudents, key = { it.id }) { student ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(student.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val json = state.results[student.id]?.studentSessionResult?.data ?: ""
                        if (state.sessionType?.tool == "chrono") {
                            ChronoEvaluationComponent(
                                jsonData = json,
                                onDataUpdated = { newJson -> onUpdateResultJson(student.id, newJson) }
                            )
                        } else {
                            DefaultEvaluationComponent(
                                jsonData = json,
                                onDataUpdated = { newJson -> onUpdateResultJson(student.id, newJson) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinishedContent(
    state: UiState.Success,
    onOverrideGrade: (Int, Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("State: FINISHED", style = MaterialTheme.typography.titleMedium)
        Text("Max Scale Evaluated: ${state.currentGradeDisplay}")
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(state.groupStudents, key = { it.id }) { student ->
                val result = state.results[student.id]
                var overrideInput by remember { mutableStateOf("") }
                
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(student.name, style = MaterialTheme.typography.titleMedium)
                        if (result != null) {
                            val computedDisplayGrade = (result.sessionResult?.grade ?: 0f) * state.currentGradeDisplay
                            val isOverridden = result.studentSessionResult.updated
                            Text("Stored JSON Metrics: ${result.studentSessionResult.data}", style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Final Evaluated Score: $computedDisplayGrade ${if (isOverridden) "(Overridden)" else ""}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        } else {
                            Text("No Evaluation Occurred")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = overrideInput,
                            onValueChange = { overrideInput = it },
                            label = { Text("Manual Grade Override") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                overrideInput.toFloatOrNull()?.let {
                                    onOverrideGrade(student.id, it)
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Apply Override")
                        }
                    }
                }
            }
        }
    }
}
