package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.skite.R
import com.example.skite.data.converters.ResultTypeData
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.entities.resultType.ResultType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ResultTypeContent(
    resultTypes: List<ResultType>,
    onSave: (ResultType) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingId by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedTool by remember { mutableStateOf(SessionTool.NONE) }

    var skillNames by remember { mutableStateOf(listOf("")) }
    var skillRatios by remember { mutableStateOf(listOf("100")) }

    val isEditing = editingId != 0

    val isSaveEnabled = name.isNotBlank() && when (selectedTool) {
        SessionTool.NONE -> buildSkillConfigurations(skillNames, skillRatios) != null
        // TODO: add validation for TIMER when implemented
        else -> false
    }

    fun resetForm() {
        editingId = 0
        name = ""
        selectedTool = SessionTool.NONE
        skillNames = listOf("")
        skillRatios = listOf("100")
    }

    ConfiguratorLayout(
        formTitle = if (isEditing)
            stringResource(R.string.settings_edit_result_type)
        else
            stringResource(R.string.settings_create_result_type),
        listTitle = stringResource(R.string.settings_existing_result_types),
        isEditing = isEditing,
        isSaveEnabled = isSaveEnabled,
        onSave = {
            val data = when (selectedTool) {
                SessionTool.NONE -> {
                    val configs = buildSkillConfigurations(skillNames, skillRatios)
                        ?: return@ConfiguratorLayout
                    Json.encodeToString(ResultTypeData(configs))
                }
                // TODO: serialize TIMER data when implemented
                else -> return@ConfiguratorLayout
            }
            onSave(
                ResultType(
                    id = editingId,
                    name = name,
                    toolName = selectedTool.value,
                    data = data
                )
            )
            resetForm()
        },
        onCancel = { resetForm() },
        formContent = {
            @Suppress("AssignedValueIsNeverRead")
            ResultTypeForm(
                name = name,
                onNameChanged = { name = it },
                selectedTool = selectedTool,
                onToolSelected = { tool ->
                    selectedTool = tool
                    skillNames = listOf("")
                    skillRatios = listOf("100")
                },
                skillNames = skillNames,
                skillRatios = skillRatios,
                onSkillNameChanged = { index, value ->
                    skillNames = skillNames.toMutableList().also { it[index] = value }
                },
                onSkillRatioChanged = { index, value ->
                    skillRatios = skillRatios.toMutableList().also { it[index] = value }
                },
                onAddSkill = {
                    skillNames = skillNames + ""
                    skillRatios = skillRatios + "0"
                },
                onRemoveSkill = {
                    skillNames = skillNames.dropLast(1)
                    skillRatios = skillRatios.dropLast(1)
                }
            )
        },
        items = resultTypes,
        itemContent = { resultType ->
            @Suppress("AssignedValueIsNeverRead")
            ResultTypeCard(
                resultType = resultType,
                onClick = {
                    editingId = resultType.id
                    name = resultType.name
                    selectedTool = SessionTool.entries
                        .find { it.value == resultType.toolName } ?: SessionTool.NONE
                    // TODO: parse resultType.data JSON to repopulate builder state
                    // when editing an existing ResultType
                }
            )
        },
        modifier = modifier.fillMaxSize()
    )
}