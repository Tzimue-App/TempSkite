package com.example.skite.ui.screens.groupScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.data.entities.group.Group
import com.example.skite.ui.common.dialogComponent.AddEntityDialog
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.common.formComponent.SearchBar
import com.example.skite.ui.common.classComponent.TrailingAction
import com.example.skite.ui.components.groups.GroupListContent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.groupViewModels.GroupListViewModel
import com.example.skite.ui.viewModels.groupViewModels.GroupListViewModel.UiState

@Composable
fun GroupListScreen(
    openDrawer: () -> Unit,
    onGroupClick: (Int) -> Unit,
    viewModel: GroupListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GroupListScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onGroupClick = onGroupClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onRetry = viewModel::retry,
        onAddGroup = viewModel::addGroup
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onGroupClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onAddGroup: (Group) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val (showAddDialog, setShowAddDialog) = remember { mutableStateOf(false) }

    val searchQuery = when (uiState) {
        is UiState.Success   -> uiState.searchQuery
        is UiState.NoResults -> uiState.query
        else                 -> ""
    }

    WithDrawer(
        title = stringResource(R.string.group_screen_groups_title),
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior
    ) {
        if (showAddDialog) {
            AddEntityDialog(
                title = stringResource(R.string.group_list_screen_new_group),
                template = Group(),
                onDismiss = { setShowAddDialog(false) },
                onSave = { newGroup ->
                    onAddGroup(newGroup)
                    setShowAddDialog(false)
                }
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            if (uiState is UiState.Success ||
                uiState is UiState.Empty    ||
                uiState is UiState.NoResults
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = onSearchQueryChanged,
                    placeholderText = stringResource(R.string.group_list_screen_search_bar),
                    trailingAction = TrailingAction(
                        icon = Icons.Default.Add,
                        contentDescription = null,
                        onClick = { setShowAddDialog(true) }
                    )
                )
            }

            when (uiState) {
                is UiState.Loading ->
                    LoadingContent()

                is UiState.Empty ->
                    EmptyContent(
                        message = stringResource(R.string.no_data),
                        modifier = Modifier.padding(top = 100.dp)
                    )

                is UiState.NoResults ->
                    EmptyContent(
                        message = stringResource(R.string.search_bar_no_result, uiState.query),
                        modifier = Modifier.padding(top = 100.dp)
                    )

                is UiState.Error ->
                    ErrorContent(
                        message = uiState.message ?: stringResource(R.string.generic_error),
                        onRetry = onRetry
                    )

                is UiState.Success ->
                    GroupListContent(
                        uiState = uiState,
                        onGroupClick = onGroupClick
                    )
            }
        }
    }
}