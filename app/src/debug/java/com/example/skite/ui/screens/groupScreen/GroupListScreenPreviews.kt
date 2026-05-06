package com.example.skite.ui.screens.groupScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.group.Group
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.components.groups.GroupListContent
import com.example.skite.ui.viewModels.groupViewModels.GroupListViewModel.UiState

@Preview(showBackground = true)
@Composable
private fun GroupListContentSuccessPreview() {
    MaterialTheme {
        GroupListContent(
            uiState = UiState.Success(
                groups = listOf(
                    Group(id = 1, name = "Lundi 1-2", desc = "Cours du Lundi 1e et 2eme heure", year = 4),
                    Group(id = 2, name = "Mardi 3-4", desc = "Cours du mardi 3eme et 4eme heure", year = 5)
                ),
                searchQuery = ""
            ),
            onGroupClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListContentLoadingPreview() {
    MaterialTheme {
        LoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListContentEmptyPreview() {
    MaterialTheme {
        EmptyContent(
            message = "No Content"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListContentNoResultsPreview() {
    MaterialTheme {
        EmptyContent(
            message = "Not found"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupListContentErrorPreview() {
    MaterialTheme {
        ErrorContent(
            message = "Error",
            onRetry = { }
        )
    }
}
