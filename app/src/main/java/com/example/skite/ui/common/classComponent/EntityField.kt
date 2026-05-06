package com.example.skite.ui.common.classComponent

import com.example.skite.ui.common.classComponent.FieldType

data class EntityField(
    val name: String,
    val label: String,
    val value: Any?,
    val type: FieldType
)