package com.example.skite.ui.components.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skite.data.converters.ResultTypeData
import com.example.skite.data.converters.StudentResultData
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.ui.common.dialogComponent.OptionsDialog
import com.example.skite.ui.common.listComponent.cardListSection
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.UiState
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionFinishedNoneContent(
    uiState: UiState.Success,
    onUpdateAttendance: (Map<Int, SessionAttendance>) -> Unit,
    onUpdateResultJson: (Int, Map<String, Float>) -> Unit,
    onOverrideGrade: (Int, Float) -> Unit,
    onGradeDisplayChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var isStudentListExpanded by rememberSaveable { mutableStateOf(true) }

    val localAttendances = rememberSaveable (uiState.groupStudents, uiState.attendances) {
        mutableStateMapOf<Int, SessionAttendance>().apply {
            uiState.groupStudents.forEach { student ->
                put(student.id, uiState.attendances[student.id]?.attendance ?: SessionAttendance.PRESENT)
            }
        }
    }

    val resultTypeData = remember(uiState.resultType) {
        uiState.resultType?.data?.let {
            try {
                Json.decodeFromString<ResultTypeData>(it)
            } catch (e: Exception) {
                null
            }
        }
    } ?: ResultTypeData()

    val initialAttendances = remember(uiState.groupStudents, uiState.attendances) {
        uiState.groupStudents.associate { student ->
            student.id to (uiState.attendances[student.id]?.attendance ?: SessionAttendance.PRESENT)
        }
    }

    val initialGrades = remember(uiState.groupStudents, uiState.results, resultTypeData) {
        uiState.groupStudents.associate { student ->
            val existingResult = uiState.results[student.id]?.studentSessionResult
            val existingData = existingResult?.data?.let {
                try {
                    Json.decodeFromString<StudentResultData>(it)
                } catch (e: Exception) {
                    null
                }
            }
            val skillsMap = existingData?.skills?.associate { it.skillName to it.score } ?: emptyMap()
            
            student.id to resultTypeData.skills.associate { config ->
                config.name to (skillsMap[config.name] ?: 0f)
            }.toMutableMap()
        }
    }

    val initialGlobalGrades = remember(uiState.groupStudents, uiState.results) {
        uiState.groupStudents.associate { student ->
            student.id to (uiState.results[student.id]?.sessionResult?.grade ?: 0f)
        }
    }

    val initialOverriddenFlags = remember(uiState.groupStudents, uiState.results) {
        uiState.groupStudents.associate { student ->
            student.id to (uiState.results[student.id]?.studentSessionResult?.updated ?: false)
        }
    }

    val editedAttendances = remember(initialAttendances) { mutableStateMapOf<Int, SessionAttendance>().apply { putAll(initialAttendances) } }
    val editedGrades = remember(initialGrades) {
        mutableStateMapOf<Int, MutableMap<String, Float>>().apply {
            putAll(initialGrades.mapValues { it.value.toMutableMap() })
        }
    }
    val editedGlobalGrades = remember(initialGlobalGrades) { mutableStateMapOf<Int, Float>().apply { putAll(initialGlobalGrades) } }
    val overriddenFlags = remember(initialOverriddenFlags) { mutableStateMapOf<Int, Boolean>().apply { putAll(initialOverriddenFlags) } }

    fun computeGlobalGrade(studentId: Int): Float {
        val studentSkills = editedGrades[studentId] ?: return 0f
        var weightedSum = 0f
        var totalRatio = 0f
        resultTypeData.skills.forEach { config ->
            val score = studentSkills[config.name] ?: 0f
            weightedSum += score * config.ratio
            totalRatio += config.ratio
        }
        return if (totalRatio > 0) weightedSum / totalRatio else 0f
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showOptionsDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Options")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cardListSection(
                    sectionKey = "finished_students",
                    sectionTitle = "Résultats des étudiants",
                    entities = uiState.groupStudents,
                    isExpanded = isStudentListExpanded,
                    onToggle = { isStudentListExpanded = !isStudentListExpanded }
                ) { student ->
                    val attendance = editedAttendances[student.id] ?: SessionAttendance.PRESENT
                    val globalGrade = editedGlobalGrades[student.id] ?: 0f
                    val isOverridden = overriddenFlags[student.id] ?: false
                    val currentScores = editedGrades[student.id] ?: emptyMap()

                    StudentEvaluationFinishedCard(
                        student = student,
                        attendance = attendance,
                        onAttendanceChanged = { newAttendance ->
                            editedAttendances[student.id] = newAttendance
                        },
                        skills = resultTypeData.skills,
                        currentScores = currentScores,
                        globalGrade = globalGrade,
                        isOverridden = isOverridden,
                        onScoreChanged = { skillName, score ->
                            editedGrades[student.id]?.set(skillName, score)
                            val newGlobal = computeGlobalGrade(student.id)
                            editedGlobalGrades[student.id] = newGlobal
                            overriddenFlags[student.id] = false
                        },
                        onOverrideGrade = { newOverrideGrade ->
                            editedGlobalGrades[student.id] = newOverrideGrade
                            overriddenFlags[student.id] = true
                        },
                        gradeDisplay = uiState.currentGradeDisplay
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    uiState.groupStudents.forEach { student ->
                        val att = editedAttendances[student.id] ?: SessionAttendance.PRESENT
                        // 1. Save attendance
                        onUpdateAttendance(localAttendances)

                        // 2. Save grades
                        if (att == SessionAttendance.PRESENT) {
                            val isOverridden = overriddenFlags[student.id] ?: false
                            if (isOverridden) {
                                onOverrideGrade(student.id, editedGlobalGrades[student.id] ?: 0f)
                            } else {
                                val grades = editedGrades[student.id] ?: emptyMap()
                                onUpdateResultJson(student.id, grades)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Enregistrer les modifications",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showOptionsDialog) {
        OptionsDialog(
            title = "Options de session",
            onDismiss = { showOptionsDialog = false }
        ) {
            Text(
                text = "Référentiel de note",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val scales = listOf(10, 20, 100)
                scales.forEach { scale ->
                    val isSelected = uiState.currentGradeDisplay == scale
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGradeDisplayChanged(scale) },
                        label = { Text("Sur $scale") }
                    )
                }
            }
        }
    }
}
