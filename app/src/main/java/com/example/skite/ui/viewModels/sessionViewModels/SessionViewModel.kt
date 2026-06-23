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
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.group.GroupWithStudents
import com.example.skite.data.repositories.*
import com.example.skite.data.converters.ResultTypeData
import com.example.skite.data.converters.StudentResultData
import com.example.skite.data.converters.StudentSkillResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
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
    private val resultTypeRepository: ResultTypeRepository,
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
            val resultType: ResultType?,
            val groupStudents: List<Student>,
            val presentStudents: List<Student>,
            val attendances: Map<Int, Attendance>,
            val results: Map<Int, StudentSessionResultWithSessionResult>,
            val currentGradeDisplay: Int
        ) : UiState()
    }

    private data class DataBundle(
        val groupRes: DataResult<GroupWithStudents>,
        val typeRes: DataResult<SessionType?>,
        val resultTypeRes: DataResult<ResultType?>,
        val studentResultsRes: DataResult<List<StudentSessionResultWithSessionResult>>
    )

    companion object {
        private const val ARG_SESSION_ID = "sessionId"
    }

    private val sessionId: Int = savedStateHandle[ARG_SESSION_ID]
        ?: error("sessionId missing from SavedStateHandle")

    // TODO: This config needs to be set at the appConfig or SessionConfig level
    private val _manualGradeDisplay = MutableStateFlow<Int?>(null)
    private val _manualError = MutableStateFlow<UiState.Error?>(null)

    val uiState: StateFlow<UiState> = sessionRepository.findWithAttendancesFlow(sessionId)
        .flatMapLatest { result ->
            when (result) {
                is DataResult.Error -> flowOf(UiState.Error(result.error.message))
                is DataResult.Success -> {
                    val data = result.data
                    val session = data.session
                    val attendances = data.attendances.associateBy { it.studentId }

                    val sessionTypeFlow = session.sessionTypeId?.let {
                        sessionTypeRepository.findByIdFlow(it)
                    } ?: flowOf(DataResult.Success(null))

                    val resultTypeFlow = sessionTypeFlow.flatMapLatest { sessionTypeResult ->
                        val sessionType = (sessionTypeResult as? DataResult.Success)?.data
                        if (sessionType != null) {
                            resultTypeRepository.findByIdFlow(sessionType.resultTypeId)
                        } else {
                            flowOf(DataResult.Success(null))
                        }
                    }

                    val dataFlow = combine(
                        groupRepository.findWithStudentsFlow(session.groupId),
                        sessionTypeFlow,
                        resultTypeFlow,
                        studentSessionResultRepository.findWithSessionResultBySessionIdFlow(session)
                    ) { groupRes, typeRes, resultTypeRes, studentResultsRes ->
                        DataBundle(groupRes, typeRes, resultTypeRes, studentResultsRes)
                    }

                    combine(
                        dataFlow,
                        _manualGradeDisplay,
                        _manualError
                    ) { bundle, manualGrade, manualError ->
                        if (manualError != null) return@combine manualError

                        when (val groupRes = bundle.groupRes) {
                            is DataResult.Error -> UiState.Error(
                                message = groupRes.error.message,
                                retryable = groupRes.error !is DatabaseError.EntityNotFound
                            )
                            is DataResult.Success -> {
                                val students = groupRes.data.students
                                val presentStudents = students.filter { student ->
                                    val att = attendances[student.id]?.attendance ?: SessionAttendance.PRESENT
                                    att == SessionAttendance.PRESENT
                                }
                                val sessionType = (bundle.typeRes as? DataResult.Success)?.data
                                val resultType = (bundle.resultTypeRes as? DataResult.Success)?.data
                                val results = (bundle.studentResultsRes as? DataResult.Success)
                                    ?.data
                                    ?.associateBy { it.studentSessionResult.studentId }
                                    ?: emptyMap()

                                UiState.Success(
                                    session = session,
                                    sessionType = sessionType,
                                    resultType = resultType,
                                    groupStudents = students,
                                    presentStudents = presentStudents,
                                    attendances = attendances,
                                    results = results,
                                    currentGradeDisplay = manualGrade ?: sessionType?.defaultGradeDisplay ?: 100
                                )
                            }
                        }
                    }
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

    fun setGradeDisplay(gradeDisplay: Int) {
        _manualGradeDisplay.value = gradeDisplay
    }

    fun updateAttendance(attendances: Map<Int, SessionAttendance>) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            for ((studentId, status) in attendances) {
                val existing = state.attendances[studentId]
                val result = if (existing != null) {
                    attendanceRepository.update(existing.copy(attendance = status))
                } else {
                    attendanceRepository.add(
                        Attendance(sessionId = sessionId, studentId = studentId, attendance = status)
                    )
                }
                if (result is DataResult.Error) {
                    _manualError.value = UiState.Error(message = result.error.message, retryable = true)
                    return@launch
                }
            }
        }
    }

    fun saveStudentResult(studentId: Int, skillScores: Map<String, Float>) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val resultTypeData = state.resultType?.data?.let {
                try {
                    Json.decodeFromString<ResultTypeData>(it)
                } catch (e: Exception) {
                    // TODO: implement exception handler in the future
                    null
                }
            } ?: ResultTypeData()

            // Calculate global grade
            var weightedSum = 0f
            var totalRatio = 0f
            resultTypeData.skills.forEach { config ->
                val score = skillScores[config.name] ?: 0f
                weightedSum += score * config.ratio
                totalRatio += config.ratio
            }
            val globalGrade = if (totalRatio > 0) weightedSum / totalRatio else 0f

            // Serialize
            val skillResults = resultTypeData.skills.map { config ->
                StudentSkillResult(
                    skillName = config.name,
                    score = skillScores[config.name] ?: 0f,
                    ratio = config.ratio
                )
            }
            val json = Json.encodeToString(StudentResultData(skills = skillResults, manualOverride = false))

            val existingRelation = state.results[studentId]
            val existingStudentResult = existingRelation?.studentSessionResult

            // Upsert StudentSessionResult
            val savedStudentResultId = if (existingStudentResult != null) {
                val updateResult = studentSessionResultRepository.update(
                    existingStudentResult.copy(data = json, updated = false)
                )
                if (updateResult is DataResult.Error) {
                    _manualError.value = UiState.Error(message = updateResult.error.message, retryable = true)
                    return@launch
                }
                existingStudentResult.id
            } else {
                val addResult = studentSessionResultRepository.add(
                    StudentSessionResult(sessionId = sessionId, studentId = studentId, data = json, updated = false)
                )
                when (addResult) {
                    is DataResult.Success -> addResult.data.toInt()
                    is DataResult.Error -> {
                        _manualError.value = UiState.Error(message = addResult.error.message, retryable = true)
                        return@launch
                    }
                }
            }

            // Upsert SessionResult
            if (savedStudentResultId > 0) {
                val existingSessionResult = existingRelation?.sessionResult
                val sessionResultOperation = if (existingSessionResult != null) {
                    sessionResultRepository.update(existingSessionResult.copy(grade = globalGrade))
                } else {
                    sessionResultRepository.add(
                        SessionResult(studentSessionResultId = savedStudentResultId, grade = globalGrade)
                    )
                }
                if (sessionResultOperation is DataResult.Error) {
                    _manualError.value = UiState.Error(message = sessionResultOperation.error.message, retryable = true)
                }
            }
        }
    }

    fun overrideFinalGrade(studentId: Int, rawGrade: Float) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val existingRelation = state.results[studentId]
            val existingStudentResult = existingRelation?.studentSessionResult

            // Upsert StudentSessionResult with updated = true
            val savedStudentResultId = if (existingStudentResult != null) {
                val updateResult = studentSessionResultRepository.update(
                    existingStudentResult.copy(updated = true)
                )
                if (updateResult is DataResult.Error) {
                    _manualError.value = UiState.Error(message = updateResult.error.message, retryable = true)
                    return@launch
                }
                existingStudentResult.id
            } else {
                val addResult = studentSessionResultRepository.add(
                    StudentSessionResult(sessionId = sessionId, studentId = studentId, updated = true)
                )
                when (addResult) {
                    is DataResult.Success -> addResult.data.toInt()
                    is DataResult.Error -> {
                        _manualError.value = UiState.Error(message = addResult.error.message, retryable = true)
                        return@launch
                    }
                }
            }

            // Upsert SessionResult with the raw grade
            if (savedStudentResultId > 0) {
                val existingSessionResult = existingRelation?.sessionResult
                val sessionResultOperation = if (existingSessionResult != null) {
                    sessionResultRepository.update(existingSessionResult.copy(grade = rawGrade))
                } else {
                    sessionResultRepository.add(
                        SessionResult(studentSessionResultId = savedStudentResultId, grade = rawGrade)
                    )
                }
                if (sessionResultOperation is DataResult.Error) {
                    _manualError.value = UiState.Error(message = sessionResultOperation.error.message, retryable = true)
                }
            }
        }
    }

    fun startSession(attendances: Map<Int, SessionAttendance>) {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch

            for ((studentId, status) in attendances) {
                val existing = state.attendances[studentId]
                val result = if (existing != null) {
                    attendanceRepository.update(existing.copy(attendance = status))
                } else {
                    attendanceRepository.add(
                        Attendance(sessionId = sessionId, studentId = studentId, attendance = status)
                    )
                }
                if (result is DataResult.Error) {
                    _manualError.value = UiState.Error(message = result.error.message, retryable = true)
                    return@launch
                }
            }

            val result = sessionRepository.update(state.session.copy(state = SessionState.IN_PROGRESS))
            if (result is DataResult.Error) {
                _manualError.value = UiState.Error(message = result.error.message, retryable = true)
            }
        }
    }

    fun finishSession() {
        viewModelScope.launch {
            val state = uiState.value as? UiState.Success ?: return@launch
            val result = sessionRepository.update(state.session.copy(state = SessionState.FINISHED))
            if (result is DataResult.Error) {
                _manualError.value = UiState.Error(message = result.error.message, retryable = true)
            }
        }
    }
}