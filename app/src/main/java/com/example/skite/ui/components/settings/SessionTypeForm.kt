package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.entities.resultType.ResultType
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionTypeForm(
    name: String,
    onNameChanged: (String) -> Unit,
    selectedTool: SessionTool,
    onToolSelected: (SessionTool) -> Unit,
    filteredResultTypes: List<ResultType>,
    selectedResultType: ResultType?,
    onResultTypeSelected: (ResultType) -> Unit,
    defaultGradeDisplay: Int,
    onDefaultGradeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var toolExpanded by remember { mutableStateOf(false) }
    var resultExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text(stringResource(R.string.settings_session_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tool dropdown
        ExposedDropdownMenuBox(
            expanded = toolExpanded,
            onExpandedChange = { toolExpanded = !toolExpanded }
        ) {
            OutlinedTextField(
                value = selectedTool.name,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.settings_tool_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toolExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
            )
            ExposedDropdownMenu(
                expanded = toolExpanded,
                onDismissRequest = { toolExpanded = false }
            ) {
                SessionTool.entries.forEach { tool ->
                    DropdownMenuItem(
                        text = { Text(tool.name) },
                        onClick = {
                            onToolSelected(tool)
                            toolExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = resultExpanded,
            onExpandedChange = { resultExpanded = !resultExpanded }
        ) {
            OutlinedTextField(
                value = selectedResultType?.name
                    ?: stringResource(R.string.settings_result_type_placeholder),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.settings_result_template_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = resultExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
            )
            ExposedDropdownMenu(
                expanded = resultExpanded,
                onDismissRequest = { resultExpanded = false }
            ) {
                if (filteredResultTypes.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_no_templates)) },
                        onClick = { resultExpanded = false }
                    )
                } else {
                    filteredResultTypes.forEach { resultType ->
                        DropdownMenuItem(
                            text = { Text(resultType.name) },
                            onClick = {
                                onResultTypeSelected(resultType)
                                resultExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = defaultGradeDisplay.toString(),
            onValueChange = { newVal ->
                newVal.toIntOrNull()?.let { onDefaultGradeChanged(it) }
            },
            label = { Text(stringResource(R.string.settings_default_grade_label)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}