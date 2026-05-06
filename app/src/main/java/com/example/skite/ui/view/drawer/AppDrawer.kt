package com.example.skite.ui.view.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.skite.R
import com.example.skite.ui.navigation.Screen

@Composable
fun AppDrawer(
    navController: NavController,
    onDestinationClicked: (String) -> Unit
) {
    val destinations = listOf(
        DrawerItemData(
            stringResource(R.string.home_title),
            Screen.Index.route,
            Icons.Default.Home
        ),
        DrawerItemData(
            stringResource(R.string.group_screen_groups_title),
            Screen.GroupList.route,
            Icons.Default.Group
        ),
        DrawerItemData(
            stringResource(R.string.sessions_title),
            Screen.SessionList.route,
            Icons.Default.DateRange
        ),
        DrawerItemData(
            stringResource(R.string.temp_students_title),
            Screen.StudentList.route,
            Icons.Default.AssignmentInd
        ),
        DrawerItemData(
            stringResource(R.string.settings_title),
            Screen.Settings.route,
            Icons.Default.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp))

        Spacer(modifier = Modifier.height(8.dp))

        destinations.forEach { item ->
            NavigationDrawerItem(
                label   = { Text(item.label) },
                icon    = { Icon(item.icon, contentDescription = item.label) },
                selected = item.route == currentRoute,
                onClick  = { onDestinationClicked(item.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}