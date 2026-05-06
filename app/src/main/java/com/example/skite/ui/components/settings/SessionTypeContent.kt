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
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType

@Composable
fun SessionTypeContent(
    sessionTypes: List<SessionType>,
    resultTypes: List<ResultType>,
    onSave: (SessionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingId by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedTool by remember { mutableStateOf(SessionTool.NONE) }
    var selectedResultType by remember { mutableStateOf<ResultType?>(null) }
    var defaultGradeDisplay by remember { mutableIntStateOf(100) }

    val filteredResultTypes = resultTypes.filter { it.toolName == selectedTool.value }
    val isEditing = editingId != 0
    val isSaveEnabled = name.isNotBlank() && selectedResultType != null

    fun resetForm() {
        editingId = 0
        name = ""
        selectedTool = SessionTool.NONE
        selectedResultType = null
        defaultGradeDisplay = 100
    }

    ConfiguratorLayout(
        formTitle = if (isEditing)
            stringResource(R.string.settings_edit_session_type)
        else
            stringResource(R.string.settings_create_session_type),
        listTitle = stringResource(R.string.settings_existing_session_types),
        isEditing = isEditing,
        isSaveEnabled = isSaveEnabled,
        onSave = {
            val resultId = selectedResultType?.id ?: return@ConfiguratorLayout
            onSave(
                SessionType(
                    id = editingId,
                    name = name,
                    tool = selectedTool.value,
                    resultTypeId = resultId,
                    defaultGradeDisplay = defaultGradeDisplay
                )
            )
            resetForm()
        },
        onCancel = { resetForm() },
        formContent = {
            @Suppress("AssignedValueIsNeverRead")
            SessionTypeForm(
                name = name,
                onNameChanged = { name = it },
                selectedTool = selectedTool,
                onToolSelected = { tool ->
                    selectedTool = tool
                    selectedResultType = null
                },
                filteredResultTypes = filteredResultTypes,
                selectedResultType = selectedResultType,
                onResultTypeSelected = { selectedResultType = it },
                defaultGradeDisplay = defaultGradeDisplay,
                onDefaultGradeChanged = { defaultGradeDisplay = it }
            )
        },
        items = sessionTypes,
        itemContent = { sessionType ->
            val boundResult = resultTypes.find { it.id == sessionType.resultTypeId }
            @Suppress("AssignedValueIsNeverRead")
            SessionTypeCard(
                sessionType = sessionType,
                boundResultName = boundResult?.name,
                onClick = {
                    editingId = sessionType.id
                    name = sessionType.name
                    selectedTool = SessionTool.entries
                        .find { it.value == sessionType.tool } ?: SessionTool.NONE
                    selectedResultType = boundResult
                    defaultGradeDisplay = sessionType.defaultGradeDisplay
                }
            )
        },
        modifier = modifier.fillMaxSize()
    )
}