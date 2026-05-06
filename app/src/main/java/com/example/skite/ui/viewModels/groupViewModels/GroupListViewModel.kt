package com.example.skite.ui.viewModels.groupViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.group.Group
import com.example.skite.data.repositories.GroupRepository
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class NoResults(val query: String) : UiState()
        data class Error(val message: String?) : UiState()
        data class Success(
            val groups: List<Group>,
            val searchQuery: String = ""
        ) : UiState()
    }

    private val _searchQuery = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiState> = combine(
        groupRepository.findAllFlow(),
        _searchQuery,
        _error
    ) { result, query, error ->
        if (error != null) return@combine UiState.Error(error)

        when (result) {
            is DataResult.Success -> {
                val groups = result.data
                if (groups.isEmpty()) {
                    UiState.Empty
                } else {
                    val filtered = if (query.isBlank()) groups
                    else groups.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.year.toString().contains(query)
                    }
                    if (filtered.isEmpty()) UiState.NoResults(query)
                    else UiState.Success(filtered, query)
                }
            }
            is DataResult.Error -> UiState.Error(result.error.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun retry() {
        _error.value = null
    }

    fun addGroup(group: Group) {
        viewModelScope.launch {
            groupRepository.add(group)
        }
    }
}