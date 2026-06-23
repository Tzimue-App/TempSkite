package com.example.skite.ui.screens.groupScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skite.R
import com.example.skite.data.entities.group.Group
import com.example.skite.ui.common.dialogComponent.AddEntityDialog
import com.example.skite.ui.common.dialogComponent.OptionsDialog
import com.example.skite.ui.common.formComponent.SearchBar
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
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

    // États de contrôle des boîtes de dialogue
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

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
        Scaffold(
            floatingActionButton = {
                if (uiState is UiState.Success || uiState is UiState.Empty || uiState is UiState.NoResults) {
                    FloatingActionButton(onClick = { showOptionsDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Options de groupe")
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState is UiState.Success ||
                    uiState is UiState.Empty    ||
                    uiState is UiState.NoResults
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChanged = onSearchQueryChanged,
                        placeholderText = stringResource(R.string.group_list_screen_search_bar),
                        trailingAction = null
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


        if (showOptionsDialog) {
            //TODO Option dialogue should be called by ListGroupOptionDialogue. This specific one uses the generic design with is own options
            OptionsDialog(
                title = "Options de gestion",
                onDismiss = { showOptionsDialog = false }
            ) {
                TextButton(
                    onClick = {
                        showOptionsDialog = false // Ferme le menu d'options
                        showAddDialog = true      // Ouvre le formulaire d'ajout
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("Option 1 : Ajouter un nouveau groupe")
                }

                TextButton(
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("Option 2 : Action future")
                }
            }
        }

        //TODO This dialogue should be called by GroupListOptionDialogue and not known by the ScreenContent
        if (showAddDialog) {
            AddEntityDialog(
                title = stringResource(R.string.group_list_screen_new_group),
                template = Group(),
                onDismiss = { showAddDialog = false },
                onSave = { newGroup ->
                    onAddGroup(newGroup)
                    showAddDialog = false
                }
            )
        }
    }
}