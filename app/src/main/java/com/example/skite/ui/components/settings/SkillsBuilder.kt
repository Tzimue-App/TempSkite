package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R

private const val MAX_SKILLS = 10
private const val MIN_SKILLS = 1


@Composable
fun SkillsBuilderContent(
    skillNames: List<String>,
    skillRatios: List<String>,
    onSkillNameChanged: (index: Int, value: String) -> Unit,
    onSkillRatioChanged: (index: Int, value: String) -> Unit,
    onAddSkill: () -> Unit,
    onRemoveSkill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRatio = skillRatios.sumOf { it.toIntOrNull() ?: 0 }
    val skillCount = skillNames.size

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_skills_count, skillCount, MAX_SKILLS),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row {
            Button(
                onClick = onRemoveSkill,
                enabled = skillCount > MIN_SKILLS
            ) { Text("-") }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddSkill,
                enabled = skillCount < MAX_SKILLS
            ) { Text("+") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        skillNames.forEachIndexed { index, name ->
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { onSkillNameChanged(index, it) },
                    label = { Text(stringResource(R.string.settings_skill_name, index + 1)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp, bottom = 4.dp, end = 4.dp)
                )
                OutlinedTextField(
                    value = skillRatios[index],
                    onValueChange = { newVal ->
                        if (newVal.all { it.isDigit() }) onSkillRatioChanged(index, newVal)
                    },
                    label = { Text("%") },
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(top = 4.dp, bottom = 4.dp, start = 4.dp)
                )
            }
        }

        if (totalRatio != 100) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_ratio_error, totalRatio),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Validates and converts builder state into a ratio map.
// Returns null if validation fails — caller uses this to gate the save button.
fun buildSkillConfigurations(
    skillNames: List<String>,
    skillRatios: List<String>
): List<com.example.skite.data.converters.SkillConfiguration>? {
    val totalRatio = skillRatios.sumOf { it.toIntOrNull() ?: 0 }
    if (skillNames.any { it.isBlank() } || totalRatio != 100) return null
    return skillNames.mapIndexed { index, name ->
        com.example.skite.data.converters.SkillConfiguration(
            name = name,
            ratio = (skillRatios[index].toIntOrNull() ?: 0) / 100f
        )
    }
}