package com.example.skite.ui.screens.groupScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.group.Group
import com.example.skite.ui.viewModels.groupViewModels.GroupListViewModel.UiState

@Preview(showBackground = true)
@Composable
private fun GroupListScreenContentSuccessPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.Success(
                groups = listOf(
                    Group(id = 1, name = "Lundi 1-2", desc = "Cours du Lundi 1e et 2eme heure", year = 4),
                    Group(id = 2, name = "Mardi 3-4", desc = "Cours du mardi 3eme et 4eme heure", year = 5)
                ),
                searchQuery = ""
            ),
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListScreenContentSuccessQueryPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.Success(
                groups = listOf(
                    Group(id = 1, name = "Lundi 1-2", desc = "Cours du Lundi 1e et 2eme heure", year = 4),
                    Group(id = 2, name = "Mardi 3-4", desc = "Cours du mardi 3eme et 4eme heure", year = 5)
                ),
                searchQuery = "Mardi"
            ),
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListScreenContentLoadingPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.Loading,
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListScreenContentEmptyPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.Empty,
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListScreenContentNoResultsPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.NoResults("Test Query"),
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListSCreenContentErrorPreview() {
    MaterialTheme {
        GroupListScreenContent(
            uiState = UiState.Error("Test Error"),
            openDrawer = {},
            onGroupClick = {},
            onSearchQueryChanged = {},
            onRetry = {},
            onAddGroup = {}
        )
    }
}
