package com.example.skite.ui.components.sessions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.skite.data.converters.SkillConfiguration
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.student.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEvaluationFinishedCard(
    student: Student,
    attendance: SessionAttendance,
    onAttendanceChanged: (SessionAttendance) -> Unit,
    skills: List<SkillConfiguration>,
    currentScores: Map<String, Float>,
    globalGrade: Float,
    isOverridden: Boolean,
    onScoreChanged: (String, Float) -> Unit,
    onOverrideGrade: (Float) -> Unit,
    gradeDisplay: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isPresent = attendance == SessionAttendance.PRESENT

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Collapsed Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Attendance Selector Dropdown
                        var dropDownExpanded by remember { mutableStateOf(false) }
                        Box {
                            AssistChip(
                                onClick = { dropDownExpanded = true },
                                label = { Text(attendance.value) },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                            )
                            DropdownMenu(
                                expanded = dropDownExpanded,
                                onDismissRequest = { dropDownExpanded = false }
                            ) {
                                SessionAttendance.entries.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.value) },
                                        onClick = {
                                            onAttendanceChanged(status)
                                            dropDownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Overridden Warning indicator
                        if (isPresent && isOverridden) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Désynchronisé",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Désynchronisé",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Grade / Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayGradeStr = if (isPresent) {
                        String.format("%.1f/%d", globalGrade * gradeDisplay, gradeDisplay)
                    } else {
                        "*"
                    }
                    Text(
                        text = displayGradeStr,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            // Expanded Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                    if (!isPresent) {
                        Text(
                            text = "Évaluation non applicable (l'étudiant est absent).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        // 1. Skill evaluation list
                        if (skills.isEmpty()) {
                            Text(
                                text = "Aucune compétence configurée pour cette session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else {
                            Text(
                                text = "Compétences",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            StudentSkillsGrid(
                                skills = skills,
                                currentScores = currentScores,
                                gradeDisplay = gradeDisplay,
                                onScoreChanged = onScoreChanged
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Global override section
                        Text(
                            text = "Forçage manuel de la note globale",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        var overrideInput by remember { mutableStateOf("") }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = overrideInput,
                                onValueChange = { overrideInput = it },
                                placeholder = { Text("Note globale (ex: 8.5)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    overrideInput.replace(',', '.').toFloatOrNull()?.let { enteredGrade ->
                                        val normalized = (enteredGrade / gradeDisplay).coerceIn(0f, 1f)
                                        onOverrideGrade(normalized)
                                        overrideInput = ""
                                    }
                                }
                            ) {
                                Text("Forcer")
                            }
                        }
                    }
                }
            }
        }
    }
}
