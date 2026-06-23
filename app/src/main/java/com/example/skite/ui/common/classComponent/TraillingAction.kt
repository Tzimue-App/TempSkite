package com.example.skite.ui.common.classComponent

import androidx.compose.ui.graphics.vector.ImageVector

data class TrailingAction(
    val icon: ImageVector,
    val contentDescription: String?,
    val onClick: () -> Unit
)