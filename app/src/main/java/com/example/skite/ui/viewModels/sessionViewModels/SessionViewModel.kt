package com.example.skite.ui.viewModels.sessionViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.session.StudentSessionResult
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.*
import com.example.skite.data.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val sessionRepository: SessionRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studentSessionResultRepository: StudentSessionResultRepository,
    private val sessionTypeRepository: SessionTypeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class SessionDetailUiState {
        object Loading : SessionDetailUiState()
        data class Error(val message: String?, val retryable: Boolean = true) : SessionDetailUiState()
        object NotFound : SessionDetailUiState()
        data class Success(
            val session: Session,
            val sessionType: SessionType?,
            val students: List<Student>,
            val attendances: Map<Int, Attendance>,
            val results: Map<Int, StudentSessionResult>,
            val isStudentListExpanded: Boolean = true
        ) : SessionDetailUiState()
    }

    private val sessionId: Int = savedStateHandle["sessionId"]
        ?: throw IllegalArgumentException("sessionId missing from SavedStateHandle")

    private val _uiState = MutableStateFlow<SessionDetailUiState>(SessionDetailUiState.Loading)
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    init {
        loadSessionData()
    }

    fun retry() = loadSessionData()

    private fun loadSessionData() {
        viewModelScope.launch {
            _uiState.value = SessionDetailUiState.Loading

            sessionRepository.findWithAttendancesFlow(sessionId).filterNotNull().flatMapLatest { sessionResult ->
                when (sessionResult) {
                    is DataResult.Success -> {
                        val sessionWithAtt = sessionResult.data ?: return@flatMapLatest flowOf(SessionDetailUiState.NotFound)
                        val session = sessionWithAtt.session
                        val attendances = sessionWithAtt.attendances.associateBy { it.studentId }

                        val sessionTypeFlow = if (session.sessionTypeId != null) {
                            sessionTypeRepository.findByIdFlow(session.sessionTypeId)
                        } else {
                            flowOf(DataResult.Success(null))
                        }

                        combine(
                            groupRepository.findWithStudentsFlow(session.groupId).filterNotNull(),
                            sessionTypeFlow,
                            studentSessionResultRepository.findBySessionIdFlow(session.id)
                        ) { groupRes, typeRes, resultRes ->
                            val students = if (groupRes is DataResult.Success && groupRes.data != null) {
                                groupRes.data.students
                            } else emptyList()

                            val sessionType = if (typeRes is DataResult.Success) typeRes.data else null
                            val results = if (resultRes is DataResult.Success) {
                                resultRes.data.associateBy { it.studentId }
                            } else emptyMap()

                            SessionDetailUiState.Success(
                                session = session,
                                sessionType = sessionType,
                                students = students,
                                attendances = attendances,
                                results = results
                            )
                        }
                    }
                    is DataResult.Error -> flowOf(SessionDetailUiState.Error(sessionResult.error.message))
                }
            }.collect { newState ->
                val currentExp = (_uiState.value as? SessionDetailUiState.Success)?.isStudentListExpanded ?: true
                _uiState.value = if (newState is SessionDetailUiState.Success) {
                    newState.copy(isStudentListExpanded = currentExp)
                } else newState
            }
        }
    }

    fun toggleStudentListExpanded() {
        (_uiState.value as? SessionDetailUiState.Success)?.let { state ->
            _uiState.value = state.copy(isStudentListExpanded = !state.isStudentListExpanded)
        }
    }

    fun updateAttendance(studentId: Int, sessionId: Int, status: SessionAttendance) {
        viewModelScope.launch {
            val state = _uiState.value as? SessionDetailUiState.Success ?: return@launch
            val existing = state.attendances[studentId]
            if (existing != null) {
                // UPDATE
                attendanceRepository.update(existing.copy(attendance = status))
            } else {
                // INSERT Lazy Creation
                attendanceRepository.add(
                    Attendance(sessionId = sessionId, studentId = studentId, attendance = status)
                )
            }
        }
    }

    fun updateResultJson(studentId: Int, sessionId: Int, json: String) {
        viewModelScope.launch {
            val state = _uiState.value as? SessionDetailUiState.Success ?: return@launch
            val existing = state.results[studentId]
            if (existing != null) {
                studentSessionResultRepository.update(existing.copy(data = json))
            } else {
                studentSessionResultRepository.add(
                    StudentSessionResult(sessionId = sessionId, studentId = studentId, data = json)
                )
            }
        }
    }

    fun overrideFinalGrade(studentId: Int, sessionId: Int, rawGrade: Float) {
        viewModelScope.launch {
            val state = _uiState.value as? SessionDetailUiState.Success ?: return@launch
            val existing = state.results[studentId]
            
            // Reverse math normalization based on sessionType max configuration
            val maxDisplay = 100f
            val normalized = (rawGrade / maxDisplay).coerceIn(0f, 1f)

            if (existing != null) {
                studentSessionResultRepository.update(existing.copy(updated = true))
            } else {
                studentSessionResultRepository.add(
                    StudentSessionResult(sessionId = sessionId, studentId = studentId, updated = true)
                )
            }
        }
    }

    fun startSession() {
        (_uiState.value as? SessionDetailUiState.Success)?.let { state ->
            val updated = state.session.copy(state = SessionState.IN_PROGRESS)
            viewModelScope.launch { sessionRepository.update(updated) }
        }
    }

    fun finishSession() {
        (_uiState.value as? SessionDetailUiState.Success)?.let { state ->
            val updated = state.session.copy(state = SessionState.FINISHED)
            viewModelScope.launch { sessionRepository.update(updated) }
        }
    }
}
