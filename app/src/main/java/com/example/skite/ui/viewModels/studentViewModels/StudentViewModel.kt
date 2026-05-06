package com.example.skite.ui.viewModels.studentViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.GroupRepository
import com.example.skite.data.repositories.StudentRepository
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class SessionAttendanceUiModel(
        val session: Session,
        val attendance: Attendance?
    ) : EntityWithId<Int> {
        override fun entityId(): Int = session.id
    }

    sealed class UiState {
        object Loading : UiState()
        object Empty : UiState()
        data class Error(
            val message: String?,
            val retryable: Boolean = true
        ) : UiState()
        data class Success(
            val student: Student,
            val group: Group,
            val sessionsWithAttendance: List<SessionAttendanceUiModel>,
            val isSessionListExpanded: Boolean = true
        ) : UiState()
    }

    companion object {
        private const val ARG_STUDENT_ID = "studentId"
    }

    private val studentId: Int = savedStateHandle[ARG_STUDENT_ID]
        ?: error("studentId missing from SavedStateHandle")

    private val _isSessionListExpanded = MutableStateFlow(true)
    private val _manualError = MutableStateFlow<UiState.Error?>(null)

    val uiState: StateFlow<UiState> = combine(
        studentRepository.findWithAttendancesFlow(studentId).flatMapLatest { studentResult ->
            when (studentResult) {
                is DataResult.Success -> {
                    val studentAndAttendances = studentResult.data
                    groupRepository.findWithSessionsFlow(studentAndAttendances.student.groupId)
                        .map { groupResult ->
                            when (groupResult) {
                                is DataResult.Success -> {
                                    val groupAndSessions = groupResult.data
                                    val sessionAttendanceSync = groupAndSessions.sessions.map { session ->
                                        val attendance = studentAndAttendances.attendances.find { it.sessionId == session.id }
                                        SessionAttendanceUiModel(session, attendance)
                                    }
                                    UiState.Success(
                                        student = studentAndAttendances.student,
                                        group = groupAndSessions.group,
                                        sessionsWithAttendance = sessionAttendanceSync
                                    )
                                }
                                is DataResult.Error -> UiState.Error(groupResult.error.message)
                            }
                        }
                }
                is DataResult.Error -> flowOf(UiState.Error(studentResult.error.message))
            }
        },
        _isSessionListExpanded,
        _manualError
    ) { result, isSessionListExpanded, manualError ->
        if (manualError != null) return@combine manualError
        
        when (result) {
            is UiState.Success -> result.copy(isSessionListExpanded = isSessionListExpanded)
            else -> result
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    fun toggleSessionListExpanded() {
        _isSessionListExpanded.value = !_isSessionListExpanded.value
    }

    fun retry() {
        _manualError.value = null
    }
}
