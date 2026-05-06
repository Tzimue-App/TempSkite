package com.example.skite.ui.screens.studentScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.skite.ui.common.classComponent.TrailingAction
import com.example.skite.ui.common.formComponent.SearchBar
import com.example.skite.ui.common.stateComponent.EmptyContent
import com.example.skite.ui.common.stateComponent.ErrorContent
import com.example.skite.ui.common.stateComponent.LoadingContent
import com.example.skite.ui.components.students.StudentListContent
import com.example.skite.ui.view.drawer.WithDrawer
import com.example.skite.ui.viewModels.studentViewModels.StudentListViewModel
import com.example.skite.ui.viewModels.studentViewModels.StudentListViewModel.UiState

@Composable
fun StudentListScreen(
    openDrawer: () -> Unit,
    onStudentClick: (Int) -> Unit,
    viewModel: StudentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StudentListScreenContent(
        uiState = uiState,
        openDrawer = openDrawer,
        onStudentClick = onStudentClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onRetry = viewModel::retry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreenContent(
    uiState: UiState,
    openDrawer: () -> Unit,
    onStudentClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val searchQuery = when (uiState) {
        is UiState.Success   -> uiState.searchQuery
        is UiState.NoResults -> uiState.query
        else                 -> ""
    }

    WithDrawer(
        title = stringResource(R.string.temp_students_title),
        openDrawer = openDrawer,
        scrollBehavior = scrollBehavior
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState is UiState.Success || uiState is UiState.Empty || uiState is UiState.NoResults) {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = onSearchQueryChanged,
                    placeholderText = stringResource(R.string.temp_student_list_screen_search_bar),
                    trailingAction = TrailingAction(
                        icon = Icons.Default.Add,
                        contentDescription = null,
                        onClick = {
                            // TODO: Add group selector before adding a student
                        }
                    )
                )
            }

            when (val state = uiState) {
                is UiState.Loading -> LoadingContent()
                is UiState.Empty -> EmptyContent(message = stringResource(R.string.temp_no_students), modifier = Modifier.padding(top = 100.dp))
                is UiState.NoResults -> EmptyContent(
                    message = stringResource(R.string.search_bar_no_result, state.query),
                    modifier = Modifier.padding(top = 100.dp)
                )
                is UiState.Error -> ErrorContent(
                    message = state.message ?: stringResource(R.string.generic_error),
                    onRetry = onRetry
                )
                is UiState.Success -> {
                    StudentListContent(
                        uiState = state,
                        onStudentClick = onStudentClick
                    )
                }
            }
        }
    }
}
