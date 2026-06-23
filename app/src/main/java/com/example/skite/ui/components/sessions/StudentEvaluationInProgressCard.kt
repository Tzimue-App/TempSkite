package com.example.skite.ui.components.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skite.data.converters.SkillConfiguration
import com.example.skite.data.entities.student.Student

@Composable
fun StudentEvaluationInProgressCard(
    student: Student,
    skills: List<SkillConfiguration>,
    currentScores: Map<String, Float>,
    gradeDisplay: Int,
    onScoreChanged: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            
            if (skills.isEmpty()) {
                Text(
                    text = "Aucune compétence configurée pour ce type de session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                StudentSkillsGrid(
                    skills = skills,
                    currentScores = currentScores,
                    gradeDisplay = gradeDisplay,
                    onScoreChanged = onScoreChanged
                )
            }
        }
    }
}
