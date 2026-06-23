package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.ui.common.stateComponent.EmptyContent

@Composable
fun <T : EntityWithId<Int>> ConfiguratorLayout(
    formTitle: String,
    listTitle: String,
    isEditing: Boolean,
    isSaveEnabled: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    formContent: @Composable () -> Unit,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "configurator_header") {
            Text(
                text = formTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item(key = "configurator_form") {
            formContent()
        }

        item(key = "configurator_actions") {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSave,
                enabled = isSaveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditing)
                        stringResource(R.string.settings_update)
                    else
                        stringResource(R.string.settings_save)
                )
            }
            if (isEditing) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        }

        item(key = "configurator_divider") {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = listTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (items.isEmpty()) {
            item(key = "configurator_empty") {
                EmptyContent(
                    message = stringResource(R.string.no_data),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            items(
                items = items,
                key = { "configurator_item_${it.entityId()}" }
            ) { item ->
                itemContent(item)
            }
        }
    }
}