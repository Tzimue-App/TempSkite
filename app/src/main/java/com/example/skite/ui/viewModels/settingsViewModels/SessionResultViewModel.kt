package com.example.skite.ui.viewModels.settingsViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.repositories.ResultTypeRepository
import com.example.skite.data.repositories.SessionTypeRepository
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionResultViewModel @Inject constructor(
    private val sessionTypeRepository: SessionTypeRepository,
    private val resultTypeRepository: ResultTypeRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Error(
            val message: String?,
            val retryable: Boolean = true
        ) : UiState()
        data class Success(
            val sessionTypes: List<SessionType>,
            val resultTypes: List<ResultType>
        ) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun retry() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            combine(
                sessionTypeRepository.findAllFlow(),
                resultTypeRepository.findAllFlow()
            ) { sessionResult, resultTypeResult ->
                if (sessionResult is DataResult.Success && resultTypeResult is DataResult.Success) {
                    UiState.Success(
                        sessionTypes = sessionResult.data,
                        resultTypes = resultTypeResult.data
                    )
                } else {
                    val errorMsg = (sessionResult as? DataResult.Error)?.error?.message
                        ?: (resultTypeResult as? DataResult.Error)?.error?.message
                    UiState.Error(errorMsg)
                }
            }.collect { _uiState.value = it }
        }
    }

    fun saveSessionType(sessionType: SessionType) {
        viewModelScope.launch {
            if (sessionType.id == 0) {
                sessionTypeRepository.add(sessionType)
            } else {
                sessionTypeRepository.update(sessionType)
            }
        }
    }

    fun saveResultType(resultType: ResultType) {
        viewModelScope.launch {
            if (resultType.id == 0) {
                resultTypeRepository.add(resultType)
            } else {
                resultTypeRepository.update(resultType)
            }
        }
    }

    fun deleteSessionType(sessionType: SessionType) {
        viewModelScope.launch {
            sessionTypeRepository.delete(sessionType)
        }
    }

    fun deleteResultType(resultType: ResultType) {
        viewModelScope.launch {
            resultTypeRepository.delete(resultType)
        }
    }
}