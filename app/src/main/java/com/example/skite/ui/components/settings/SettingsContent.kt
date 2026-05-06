package com.example.skite.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.skite.ui.navigation.Screen

data class SettingsCategoryItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

private val settingsCategories = listOf(
    SettingsCategoryItem(
        title = "Session & Result",
        subtitle = "Configure session types and result templates",
        icon = Icons.Default.Grade,
        route = Screen.SettingsSessionResult.route
    ),
    SettingsCategoryItem(
        title = "Example 2",
        subtitle = "Placeholder for future settings",
        icon = Icons.Default.Settings,
        route = "" // TODO: replace with Screen.SettingsExample2.route
    ),
    SettingsCategoryItem(
        title = "Example 3",
        subtitle = "Placeholder for future settings",
        icon = Icons.Default.Quiz,
        route = "" // TODO: replace with Screen.SettingsExample3.route
    )
)

@Composable
fun SettingsContent(
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = settingsCategories,
            key = { it.route.ifEmpty { it.title } }
        ) { category ->
            SettingsCategoryCard(
                item = category,
                onClick = {
                    if (category.route.isNotEmpty()) onCategoryClick(category.route)
                }
            )
        }
    }
}