package com.example.skite.ui.screens.studentScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.common.stateComponent.NotFoundContent
import com.example.skite.ui.components.students.StudentDetailContent
import com.example.skite.ui.designSystem.component.actionComponent.BackAction
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.studentViewModels.StudentViewModel
import com.example.skite.ui.viewModels.studentViewModels.StudentViewModel.UiState

@Composable
fun StudentDetailScreen(
    openDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSession: (Int) -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StudentDetailScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onSessionClick = onNavigateToSession,
        onToggleSessions = viewModel::toggleSessionListExpanded,
        onNavigateBack = onNavigateBack,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onSessionClick: (Int) -> Unit,
    onToggleSessions: () -> Unit,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    WithDrawer(
        title = when (val s = uiState) {
            is UiState.Success -> s.student.name
            else -> stringResource(R.string.temp_students_title)
        },
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior,
        actions = {
            BackAction(onNavigateBack = onNavigateBack)
        }
    ) {
        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Empty -> NotFoundContent(
                message = stringResource(R.string.temp_student_not_found),
                onNavigateBack = onNavigateBack
            )
            is UiState.Error -> ErrorContent(
                message = uiState.message ?: stringResource(R.string.generic_error),
                onRetry = onRetry
            )
            is UiState.Success -> {
                StudentDetailContent(
                    uiState = uiState,
                    onSessionClick = onSessionClick,
                    onToggleSessions = onToggleSessions
                )
            }
        }
    }
}
