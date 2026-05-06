package com.example.skite.ui.viewModels.sessionViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.session.Session
import com.example.skite.data.repositories.SessionRepository
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionListViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Error(val message: String?, val retryable: Boolean = true) : UiState()
        data class Success(val sessions: List<Session>) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun retry() = loadSessions()

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            sessionRepository.findAllFlow().collect { result ->
                _uiState.value = when (result) {
                    is DataResult.Success -> {
                        if (result.data.isEmpty()) UiState.Empty
                        else UiState.Success(result.data)
                    }
                    is DataResult.Error -> UiState.Error(
                        message = result.error.message,
                        retryable = true
                    )
                }
            }
        }
    }
}
