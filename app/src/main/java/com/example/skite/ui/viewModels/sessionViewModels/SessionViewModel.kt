package com.example.skite.ui.viewModels.sessionViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.session.SessionResult
import com.example.skite.data.entities.session.StudentSessionResult
import com.example.skite.data.entities.session.StudentSessionResultWithSessionResult
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.entities.student.Student
import com.example.skite.data.error.DatabaseError
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
    private val attendanceRepository: AttendanceRepository,
    private val studentSessionResultRepository: StudentSessionResultRepository,
    private val sessionResultRepository: SessionResultRepository,
    private val sessionTypeRepository: SessionTypeRepository,
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
            val session: Session,
            val sessionType: SessionType?,
            val groupStudents: List<Student>,
            val presentStudents: List<Student>,
            val attendances: Map<Int, Attendance>,
            val results: Map<Int, StudentSessionResultWithSessionResult>,
            val currentGradeDisplay: Int
        ) : UiState()
    }

    private val sessionId: Int = savedStateHandle["sessionId"]
        ?: throw IllegalArgumentException("sessionId missing from SavedStateHandle")

    private val _manualGradeDisplay = MutableStateFlow<Int?>(null)
    private val _manualError = MutableStateFlow<UiState.Error?>(null)

    val uiState: StateFlow<UiState> = sessionRepository.findWithAttendancesFlow(sessionId)
        .filterNotNull()
        .flatMapLatest { sessionResult ->
            if (sessionResult is DataResult.Error) {
                return@flatMapLatest flowOf(UiState.Error(sessionResult.error.message))
            }
            val sessionData = (sessionResult as DataResult.Success).data
                ?: return@flatMapLatest flowOf(UiState.Empty)

            val session = sessionData.session
            val attendances = sessionData.attendances.associateBy { it.studentId }

            val sessionTypeFlow = session.sessionTypeId?.let {
                sessionTypeRepository.findByIdFlow(it)
            } ?: flowOf(DataResult.Success(null))

            combine(
                groupRepository.findWithStudentsFlow(session.groupId).filterNotNull(),
                sessionTypeFlow,
                studentSessionResultRepository.findWithSessionResultBySessionIdFlow(session),
                _manualGradeDisplay,
                _manualError
            ) { groupRes, typeRes, resultRes, manualGrade, manualError ->
                if (manualError != null) return@combine manualError
                if (groupRes is DataResult.Error) {
                    return@combine UiState.Error(
                        message = groupRes.error.message,
                        retryable = groupRes.error !is DatabaseError.EntityNotFound
                    )
                }

                val groupData = (groupRes as DataResult.Success).data
                val students = groupData?.students ?: emptyList()

                val presentStudents = students.filter { student ->
                    val att = attendances[student.id]?.attendance ?: SessionAttendance.PRESENT
                    att == SessionAttendance.PRESENT
                }

                val sessionType = (typeRes as? DataResult.Success)?.data
                val results = (resultRes as? DataResult.Success)?.data?.associateBy { it.studentSessionResult.studentId } ?: emptyMap()

                val currentGradeDisplay = manualGrade ?: sessionType?.defaultGradeDisplay ?: 100

                UiState.Success(
                    session = session,
                    sessionType = sessionType,
                    groupStudents = students,
                    presentStudents = presentStudents,
                    attendances = attendances,
                    results = results,
                    currentGradeDisplay = currentGradeDisplay
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun retry() {
        _manualError.value = null
    }

    fun setGradeDisplay(gradeDisplay: Int) {
        _manualGradeDisplay.value = gradeDisplay
    }

    fun updateAttendance(studentId: Int, sessionId: Int, status: SessionAttendance) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val existing = state.attendances[studentId]
            if (existing != null) {
                attendanceRepository.update(existing.copy(attendance = status))
            } else {
                attendanceRepository.add(
                    Attendance(sessionId = sessionId, studentId = studentId, attendance = status)
                )
            }
        }
    }

    fun updateResultJson(studentId: Int, sessionId: Int, json: String, newGlobalGrade: Float? = null) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val existingRelation = state.results[studentId]
            val existingStudentResult = existingRelation?.studentSessionResult

            // Upsert StudentSessionResult
            val savedStudentResultId = if (existingStudentResult != null) {
                val updatedStudentResult = existingStudentResult.copy(data = json, updated = false)
                studentSessionResultRepository.update(updatedStudentResult)
                updatedStudentResult.id
            } else {
                val newStudentResult = StudentSessionResult(sessionId = sessionId, studentId = studentId, data = json, updated = false)
                val addResult = studentSessionResultRepository.add(newStudentResult)
                if (addResult is DataResult.Success) {
                    addResult.data.toInt()
                } else {
                    return@launch
                }
            }

            // Upsert SessionResult if newGlobalGrade is provided
            if (newGlobalGrade != null && savedStudentResultId > 0) {
                val existingSessionResult = existingRelation?.sessionResult
                if (existingSessionResult != null) {
                    sessionResultRepository.update(existingSessionResult.copy(grade = newGlobalGrade))
                } else {
                    sessionResultRepository.add(
                        SessionResult(studentSessionResultId = savedStudentResultId, grade = newGlobalGrade)
                    )
                }
            }
        }
    }

    fun overrideFinalGrade(studentId: Int, sessionId: Int, rawGrade: Float) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val existingRelation = state.results[studentId]
            val existingStudentResult = existingRelation?.studentSessionResult
            
            // Upsert StudentSessionResult with updated = true
            val savedStudentResultId = if (existingStudentResult != null) {
                val updatedStudentResult = existingStudentResult.copy(updated = true)
                studentSessionResultRepository.update(updatedStudentResult)
                updatedStudentResult.id
            } else {
                val newStudentResult = StudentSessionResult(sessionId = sessionId, studentId = studentId, updated = true)
                val addResult = studentSessionResultRepository.add(newStudentResult)
                if (addResult is DataResult.Success) {
                    addResult.data.toInt()
                } else {
                    return@launch
                }
            }

            // Upsert SessionResult with the normalized grade
            if (savedStudentResultId > 0) {
                val existingSessionResult = existingRelation?.sessionResult
                if (existingSessionResult != null) {
                    sessionResultRepository.update(existingSessionResult.copy(grade = rawGrade))
                } else {
                    sessionResultRepository.add(
                        SessionResult(studentSessionResultId = savedStudentResultId.toInt(), grade = rawGrade)
                    )
                }
            }
        }
    }

    fun startSession() {
        val state = uiState.value as? UiState.Success ?: return
        val updated = state.session.copy(state = SessionState.IN_PROGRESS)
        viewModelScope.launch { sessionRepository.update(updated) }
    }

    fun finishSession() {
        val state = uiState.value as? UiState.Success ?: return
        val updated = state.session.copy(state = SessionState.FINISHED)
        viewModelScope.launch { sessionRepository.update(updated) }
    }
}
