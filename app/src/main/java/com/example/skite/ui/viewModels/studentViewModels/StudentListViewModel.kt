package com.example.skite.ui.viewModels.studentViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.StudentRepository
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class NoResults(val query: String) : UiState()
        data class Error(val message: String?, val retryable: Boolean = true) : UiState()
        data class Success(val students: List<Student>, val searchQuery: String = "") : UiState()
    }

    private val _searchQuery = MutableStateFlow("")
    private val _error = MutableStateFlow<UiState.Error?>(null)

    val uiState: StateFlow<UiState> = combine(
        studentRepository.findAllFlow(),
        _searchQuery,
        _error
    ) { result, query, error ->
        if (error != null) return@combine error

        when (result) {
            is DataResult.Success -> {
                val students = result.data
                if (students.isEmpty()) {
                    UiState.Empty
                } else {
                    val filtered = if (query.isBlank()) students else students.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    if (filtered.isEmpty()) UiState.NoResults(query)
                    else UiState.Success(filtered, query)
                }
            }
            is DataResult.Error -> UiState.Error(result.error.message, true)
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
}
