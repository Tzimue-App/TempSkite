package com.example.skite.ui.screens.settingsScreen

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.data.entities.enums.AppLanguage
import com.example.skite.ui.components.settings.LanguageContent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.settingsViewModels.LanguageViewModel

@Composable
fun LanguageScreen(
    openDrawer: () -> Unit,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLang.collectAsStateWithLifecycle()

    LanguageScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        currentLanguage = currentLanguage,
        onLanguageSelected = viewModel::updateLanguage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreenContent(
    uiState: LanguageViewModel.UiState,
    openDrawer: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Log.d("Skite", "Langue actuelle: ${currentLanguage.code}")
    WithDrawer(
        title = stringResource(R.string.settings_screen_language_title),
        openDrawer = openDrawer
    ) {
        LanguageContent(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected
        )
    }
}