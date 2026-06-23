package com.example.skite.ui.screens.settingsScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.components.settings.SessionResultContent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.settingsViewModels.SessionResultViewModel
import com.example.skite.ui.viewModels.settingsViewModels.SessionResultViewModel.UiState

@Composable
fun SessionResultScreen(
    openDrawer: () -> Unit,
    viewModel: SessionResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SessionResultScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onSaveSessionType = viewModel::saveSessionType,
        onSaveResultType = viewModel::saveResultType,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionResultScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onSaveSessionType: (SessionType) -> Unit,
    onSaveResultType: (ResultType) -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    WithDrawer(
        title = stringResource(R.string.settings_session_result_title),
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior
    ) {
        when (uiState) {
            is UiState.Loading ->
                LoadingContent()

            is UiState.Error ->
                ErrorContent(
                    message = uiState.message ?: stringResource(R.string.generic_error),
                    onRetry = onRetry
                )
            is UiState.Empty ->
                EmptyContent(
                    message = ""
                )

            is UiState.Success ->
                SessionResultContent(
                    sessionTypes = uiState.sessionTypes,
                    resultTypes = uiState.resultTypes,
                    onSaveSessionType = onSaveSessionType,
                    onSaveResultType = onSaveResultType
                )
        }
    }
}