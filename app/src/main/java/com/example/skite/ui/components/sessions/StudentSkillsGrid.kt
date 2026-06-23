package com.example.skite.ui.components.sessions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skite.data.converters.SkillConfiguration

@Composable
fun StudentSkillsGrid(
    skills: List<SkillConfiguration>,
    currentScores: Map<String, Float>,
    gradeDisplay: Int,
    onScoreChanged: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val chunkedSkills = remember(skills) { skills.chunked(4) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chunkedSkills.forEach { rowSkills ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSkills.forEach { config ->
                    val currentScore = currentScores[config.name] ?: 0f
                    val displayScore = currentScore * gradeDisplay

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = config.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            var textValue by remember(displayScore) {
                                mutableStateOf(if (displayScore == 0f) "" else String.format("%.1f", displayScore))
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(28.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                            ) {
                                BasicTextField(
                                    value = textValue,
                                    onValueChange = { input ->
                                        textValue = input
                                        val number = input.replace(',', '.').toFloatOrNull()
                                        if (number != null) {
                                            val normalized = (number / gradeDisplay).coerceIn(0f, 1f)
                                            onScoreChanged(config.name, normalized)
                                        } else if (input.isEmpty()) {
                                            onScoreChanged(config.name, 0f)
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = "/$gradeDisplay",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }
                
                // Add empty spacer columns if row is not full, to align grid items correctly
                val emptySlots = 4 - rowSkills.size
                if (emptySlots > 0) {
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
