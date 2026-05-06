package com.example.skite.ui.screens.settingsScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.ui.viewModels.settingsViewModels.SettingsViewModel.UiState

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreenContent(
            uiState = UiState.Success,
            openDrawer = {},
            onCategoryClick = { }
        )
    }
}
