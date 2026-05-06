package com.example.skite.ui.screens.sessionScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.components.sessions.SessionCard
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.sessionViewModels.SessionListViewModel
import com.example.skite.ui.viewModels.sessionViewModels.SessionListViewModel.UiState

@Composable
fun SessionListScreen(
    openDrawer: () -> Unit,
    onSessionClick: (Int) -> Unit,
    viewModel: SessionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SessionListScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onSessionClick = onSessionClick,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onSessionClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    WithDrawer(
        title = stringResource(R.string.sessions_title),
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior
    ) {
        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Empty -> EmptyContent(message = stringResource(R.string.no_sessions))
            is UiState.Error -> ErrorContent(
                message = uiState.message ?: stringResource(R.string.generic_error),
                onRetry = onRetry
            )
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(
                        items = uiState.sessions,
                        key = { it.id }
                    ) { session ->
                        SessionCard(
                            session = session,
                            attendanceDisplay = false,
                            onSessionClick = onSessionClick
                        )
                    }
                }
            }
        }
    }
}
