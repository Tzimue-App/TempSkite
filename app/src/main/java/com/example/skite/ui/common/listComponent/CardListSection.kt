package com.example.skite.ui.common.listComponent

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.ui.common.formComponent.SectionHeader
import com.example.skite.ui.common.classComponent.TrailingAction

fun <T : EntityWithId<Int>> LazyListScope.cardListSection(
    sectionKey: String,
    sectionTitle: String,
    entities: List<T>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    trailingAction: TrailingAction? = null,
    itemContent: @Composable (T) -> Unit
) {
    item(key = "${sectionKey}_header") {
        SectionHeader(
            title = sectionTitle,
            isExpanded = isExpanded,
            onToggle = onToggle,
            trailingContent = trailingAction?.let { action ->
                {
                    IconButton(onClick = action.onClick) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription
                        )
                    }
                }
            }
        )
    }

    if (isExpanded) {
        items(
            items = entities,
            key = { entity -> "${sectionKey}_${entity.entityId()}" }
        ) { entity ->
            itemContent(entity)
        }
    }
}