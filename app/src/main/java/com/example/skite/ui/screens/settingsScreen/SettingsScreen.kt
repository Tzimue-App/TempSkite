package com.example.skite.ui.screens.settingsScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.ui.components.settings.SettingsContent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.settingsViewModels.SettingsViewModel
import com.example.skite.ui.viewModels.settingsViewModels.SettingsViewModel.UiState


@Composable
fun SettingsScreen(
    openDrawer: () -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onCategoryClick = onCategoryClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    WithDrawer(
        title = stringResource(R.string.settings_title),
        openDrawer = openDrawer
    ) {
        SettingsContent(onCategoryClick = onCategoryClick)
    }
}