package com.example.skite.ui.screens.sessionScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.session.Session
import com.example.skite.ui.viewModels.sessionViewModels.SessionListViewModel.UiState
import java.util.Date

@Preview(showBackground = true)
@Composable
private fun SessionListScreenSuccessPreview() {
    MaterialTheme {
        SessionListScreenContent(
            uiState = UiState.Success(
                sessions = listOf(
                    Session(id = 1, name = "Morning Run", date = Date(), groupId = 1, state = SessionState.PLANNED),
                    Session(id = 2, name = "Evening Swim", date = Date(), groupId = 1, state = SessionState.FINISHED)
                )
            ),
            openDrawer = {},
            onSessionClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionListScreenEmptyPreview() {
    MaterialTheme {
        SessionListScreenContent(
            uiState = UiState.Empty,
            openDrawer = {},
            onSessionClick = {},
            onRetry = {}
        )
    }
}
