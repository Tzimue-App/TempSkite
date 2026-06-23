package com.example.skite.ui.viewModels.groupViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.GroupRepository
import com.example.skite.data.repositories.StudentRepository
import com.example.skite.data.error.DatabaseError
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
class GroupViewModel @Inject constructor(
    //TODO Check why I have the warning "Constructor parameter is never used as a property "
    private val groupRepository: GroupRepository,
    private val studentRepository: StudentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Error(
            val message: String?,
            val retryable: Boolean = true
        ) : UiState()
        data class Success(
            val group: Group,
            val isStudentListExpanded: Boolean = false,
            val isSessionListExpanded: Boolean = false,
            val students: List<Student> = emptyList(),
            val sessions: List<Session> = emptyList()
        ) : UiState()
    }

    companion object {
        private const val ARG_GROUP_ID = "groupId"
    }

    private val groupId: Int = savedStateHandle[ARG_GROUP_ID]
        ?: error("groupId missing from SavedStateHandle")

    //TODO Change this feature to use the UI (cf SessionDetailScreen)
    private val _isStudentListExpanded = MutableStateFlow(false)
    private val _isSessionListExpanded = MutableStateFlow(false)
    private val _manualError = MutableStateFlow<UiState.Error?>(null)

    val uiState: StateFlow<UiState> = combine(
        groupRepository.findFullFlow(groupId),
        _isStudentListExpanded,
        _isSessionListExpanded,
        _manualError
    ) { result, studentsExpanded, sessionsExpanded, manualError ->
        if (manualError != null) return@combine manualError

        when (result) {
            is DataResult.Success -> {
                val data = result.data
                UiState.Success(
                    group = data.group,
                    students = data.students,
                    sessions = data.sessions,
                    isStudentListExpanded = studentsExpanded,
                    isSessionListExpanded = sessionsExpanded
                )
            }
            is DataResult.Error -> {
                UiState.Error(
                    message = result.error.message,
                    retryable = result.error !is DatabaseError.EntityNotFound
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    fun retry() {
        _manualError.value = null
    }

    //TODO This should be handle by the UI
    fun toggleStudentListExpanded() {
        _isStudentListExpanded.value = !_isStudentListExpanded.value
    }

    //TODO This should be handle by the UI
    fun toggleSessionListExpanded() {
        _isSessionListExpanded.value = !_isSessionListExpanded.value
    }

    fun addStudents(studentNames: String) {
        val currentSuccess = uiState.value as? UiState.Success ?: return

        viewModelScope.launch {
            val names = studentNames
                .split(Regex("[,\\n]"))
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (names.isEmpty()) return@launch

            val startNumber = (currentSuccess.students.maxByOrNull { it.number }?.number ?: 0) + 1

            val newStudents = names.mapIndexed { index, name ->
                Student(
                    name = name,
                    groupId = groupId,
                    number = startNumber + index
                )
            }

            when (val result = studentRepository.addAll(newStudents)) {
                is DataResult.Success -> {
                }
                is DataResult.Error -> {
                    _manualError.value = UiState.Error(
                        message = result.error.message,
                        retryable = true
                    )
                }
            }
        }
    }
}