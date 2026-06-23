package com.example.skite.ui.designSystem.component.actionComponent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.skite.R

@Composable
fun BackAction(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onNavigateBack, modifier = modifier) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}
