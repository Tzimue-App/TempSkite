package com.example.skite.ui.components.groups

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.skite.ui.common.listComponent.EntityListComponent
import com.example.skite.ui.viewModels.groupViewModels.GroupListViewModel.UiState

@Composable
fun GroupListContent(
    uiState: UiState.Success,
    onGroupClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityListComponent(
        entities = uiState.groups,
        modifier = modifier
    ) { group ->
        GroupCard(
            group = group,
            onGroupClick = { onGroupClick(group.id) }
        )
    }
}