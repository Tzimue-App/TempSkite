package com.example.skite.ui.screens.sessionScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.common.stateComponent.NotFoundContent
import com.example.skite.ui.components.sessions.SessionFinishedNoneContent
import com.example.skite.ui.components.sessions.SessionInProgressNoneContent
import com.example.skite.ui.components.sessions.SessionPlannedContent
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
        onStartSession = { attendance -> viewModel.startSession( attendance ) },
        onFinishSession = viewModel::finishSession,
        onUpdateAttendance = { attendance -> viewModel.updateAttendance(attendance) },
        onUpdateResultJson = { studentId, skillScores -> viewModel.saveStudentResult(studentId, skillScores) },
        onOverrideGrade = { studentId, grade -> viewModel.overrideFinalGrade(studentId, grade) },
        onGradeDisplayChanged = viewModel::setGradeDisplay,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    onStartSession: (Map<Int, SessionAttendance>) -> Unit,
    onFinishSession: () -> Unit,
    onUpdateAttendance: (Map<Int, SessionAttendance>) -> Unit,
    onUpdateResultJson: (Int, Map<String, Float>) -> Unit,
    onOverrideGrade: (Int, Float) -> Unit,
    onGradeDisplayChanged: (Int) -> Unit,
    onRetry: () -> Unit
) {
    WithDrawer(
        title = when (uiState) {
            is UiState.Success -> uiState.session.name
            else -> stringResource(R.string.TBD)
        },
        openDrawer = openDrawer
    ) {
        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Empty -> NotFoundContent(
                message = stringResource(R.string.TBD),
                onNavigateBack = onNavigateBack
            )
            is UiState.Error -> ErrorContent(
                message = uiState.message ?: stringResource(R.string.TBD),
                onRetry = onRetry
            )
            is UiState.Success -> {
                when (uiState.session.state) {
                    SessionState.PLANNED -> SessionPlannedContent(
                        uiState = uiState,
                        onStartSession = onStartSession,
                        onUpdateAttendance = onUpdateAttendance
                    )
                    SessionState.IN_PROGRESS -> {
                        when (uiState.sessionType?.tool) {
                            SessionTool.TIMER -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Outil Chronomètre - En développement",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            SessionTool.NONE -> {
                                SessionInProgressNoneContent(
                                    state = uiState,
                                    onFinishSession = onFinishSession,
                                    onUpdateResultJson = { studentId, skillScores ->
                                        onUpdateResultJson(
                                            studentId,
                                            skillScores
                                        )
                                    },
                                    onGradeDisplayChanged = onGradeDisplayChanged
                                )
                            }
                            else -> {
                                ErrorContent(
                                message = stringResource(R.string.TBD),
                                onRetry = onRetry
                                )
                            }
                        }
                    }
                    SessionState.FINISHED -> {
                        when (uiState.sessionType?.tool) {
                            SessionTool.TIMER -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Outil Chronomètre - En développement",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            SessionTool.NONE -> {
                                SessionFinishedNoneContent(
                                    uiState = uiState,
                                    onUpdateAttendance = onUpdateAttendance,
                                    onUpdateResultJson = { studentId, skillScores ->
                                        onUpdateResultJson(
                                            studentId,
                                            skillScores
                                        )
                                    },
                                    onOverrideGrade = { studentId, grade ->
                                        onOverrideGrade(
                                            studentId,
                                            grade
                                        )
                                    },
                                    onGradeDisplayChanged = onGradeDisplayChanged
                                )
                            }
                            else -> {
                                ErrorContent(
                                    message = stringResource(R.string.TBD),
                                    onRetry = onRetry
                                )
                            }
                        }
                    }
                    else -> {
                        //TODO set up cancel screen
                        ErrorContent(
                            message = stringResource(R.string.TBD),
                            onRetry = onRetry
                        )
                    }
                }
            }
        }
    }
}
