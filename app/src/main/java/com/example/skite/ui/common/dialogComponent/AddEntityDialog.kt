package com.example.skite.ui.common.dialogComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.skite.ui.common.classComponent.EditableEntity
import com.example.skite.ui.common.classComponent.FieldType

@Composable
fun <T : EditableEntity<*>> AddEntityDialog(
    title: String,
    template: T,
    onDismiss: () -> Unit,
    onSave: (T) -> Unit
) {
    val fields = template.getFields()
    var fieldValues by remember { mutableStateOf(fields.associate { it.name to (it.value ?: "") }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fields.forEach { field ->
                    OutlinedTextField(
                        value = fieldValues[field.name]?.toString() ?: "",
                        onValueChange = { newVal ->
                            fieldValues = fieldValues.toMutableMap().apply { put(field.name, newVal) }
                        },
                        label = { Text(field.label) },
                        keyboardOptions = if (field.type == FieldType.NUMBER) {
                            KeyboardOptions(keyboardType = KeyboardType.Number)
                        } else {
                            KeyboardOptions.Default
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    @Suppress("UNCHECKED_CAST")
                    val newEntity = template.copyWithFields(fieldValues) as T
                    onSave(newEntity)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
