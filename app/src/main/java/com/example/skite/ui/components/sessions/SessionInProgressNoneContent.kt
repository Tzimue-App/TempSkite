package com.example.skite.ui.components.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.data.converters.ResultTypeData
import com.example.skite.data.converters.StudentResultData
import com.example.skite.ui.common.dialogComponent.OptionsDialog
import com.example.skite.ui.common.listComponent.cardListSection
import com.example.skite.ui.viewModels.sessionViewModels.SessionViewModel.UiState
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionInProgressNoneContent(
    state: UiState.Success,
    onFinishSession: () -> Unit,
    onUpdateResultJson: (Int, Map<String, Float>) -> Unit,
    onGradeDisplayChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var isStudentListExpanded by rememberSaveable { mutableStateOf(true) }

    val resultTypeData = remember(state.resultType) {
        state.resultType?.data?.let {
            try {
                Json.decodeFromString<ResultTypeData>(it)
            } catch (e: Exception) {
                null
            }
        }
    } ?: ResultTypeData()

    val initialGrades = remember(state.presentStudents, state.results, resultTypeData) {
        state.presentStudents.associate { student ->
            val existingResult = state.results[student.id]?.studentSessionResult
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

    val editedGrades = remember(initialGrades) {
        mutableStateMapOf<Int, MutableMap<String, Float>>().apply {
            putAll(initialGrades.mapValues { it.value.toMutableMap() })
        }
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
            if (state.presentStudents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun étudiant marqué présent.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cardListSection(
                        sectionKey = "present_students",
                        sectionTitle = "Étudiants présents",
                        entities = state.presentStudents,
                        isExpanded = isStudentListExpanded,
                        onToggle = { isStudentListExpanded = !isStudentListExpanded }
                    ) { student ->
                        val currentScores = editedGrades[student.id] ?: emptyMap()
                        StudentEvaluationInProgressCard(
                            student = student,
                            skills = resultTypeData.skills,
                            currentScores = currentScores,
                            gradeDisplay = state.currentGradeDisplay,
                            onScoreChanged = { skillName, score ->
                                editedGrades[student.id]?.set(skillName, score)
                            }
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        state.presentStudents.forEach { student ->
                            val grades = editedGrades[student.id] ?: emptyMap()
                            onUpdateResultJson(student.id, grades)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.TBD))
                }

                Button(
                    onClick = {
                        state.presentStudents.forEach { student ->
                            val grades = editedGrades[student.id] ?: emptyMap()
                            onUpdateResultJson(student.id, grades)
                        }
                        onFinishSession()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.TBD))
                }
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
                    val isSelected = state.currentGradeDisplay == scale
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
