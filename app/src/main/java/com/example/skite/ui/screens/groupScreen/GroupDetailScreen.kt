package com.example.skite.ui.screens.groupScreen


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.skite.ui.components.groups.AddStudentDialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.common.stateComponent.NotFoundContent
import com.example.skite.ui.components.groups.GroupDetailContent
import com.example.skite.ui.designSystem.component.actionComponent.BackAction
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.groupViewModels.GroupViewModel
import com.example.skite.ui.viewModels.groupViewModels.GroupViewModel.UiState

@Composable
fun GroupDetailScreen(
    openDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSession: (Int) -> Unit,
    onNavigateToStudent: (Int) -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GroupDetailScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onToggleStudents = viewModel::toggleStudentListExpanded,
        onToggleSessions = viewModel::toggleSessionListExpanded,
        onAddStudents = viewModel::addStudents,
        onSessionClick = onNavigateToSession,
        onStudentClick = onNavigateToStudent,
        onNavigateBack = onNavigateBack,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onToggleStudents: () -> Unit,
    onToggleSessions: () -> Unit,
    onAddStudents: (String) -> Unit,
    onSessionClick: (Int) -> Unit,
    onStudentClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val (showAddDialog, setShowAddDialog) = remember { mutableStateOf(false) }

    WithDrawer(
        title = when (uiState) {
            is UiState.Success -> uiState.group.name
            else -> stringResource(R.string.group_screen_groups_title)
        },
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior,
        actions = {
            BackAction(onNavigateBack = onNavigateBack)
        }
    ) {
        if (showAddDialog) {
            AddStudentDialog(
                onDismiss = { setShowAddDialog(false) },
                onConfirm = { names ->
                    onAddStudents(names)
                    setShowAddDialog(false)
                }
            )
        }

        when (uiState) {
            is UiState.Loading ->
                LoadingContent()

            is UiState.Empty ->
                NotFoundContent(
                    message = stringResource(R.string.group_detail_screen_not_found),
                    onNavigateBack = onNavigateBack
                )

            is UiState.Error ->
                ErrorContent(
                    message = uiState.message ?: stringResource(R.string.generic_error),
                    onRetry = onRetry
                )

            is UiState.Success ->
                GroupDetailContent(
                    uiState = uiState,
                    onToggleStudents = onToggleStudents,
                    onToggleSessions = onToggleSessions,
                    onAddStudentClick = { setShowAddDialog(true) },
                    onSessionClick = onSessionClick,
                    onStudentClick = onStudentClick
                )
        }
    }
}

