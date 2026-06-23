package com.example.skite.ui.components.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.skite.R
import com.example.skite.ui.navigation.Screen

data class SettingsCategoryItem(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val icon: ImageVector,
    val route: String
)

private val settingsCategories = listOf(
    SettingsCategoryItem(
        title = R.string.settings_screen_session_result_title,
        subtitle = R.string.settings_screen_session_result_subtitle,
        icon = Icons.Default.Grade,
        route = Screen.SettingsSessionResult.route
    ),
    SettingsCategoryItem(
        title = R.string.settings_screen_language_title,
        subtitle = R.string.settings_screen_language_subtitle,
        icon = Icons.Default.Language,
        route = Screen.SettingsLanguage.route
    ),
    SettingsCategoryItem(
        title = R.string.settings_screen_calendar_title,
        subtitle = R.string.settings_screen_calendar_subtitle,
        icon = Icons.Default.EditCalendar,
        route = Screen.SettingsCalender.route
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