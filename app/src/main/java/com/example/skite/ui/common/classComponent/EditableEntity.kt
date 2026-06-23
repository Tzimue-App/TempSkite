package com.example.skite.ui.common.classComponent

interface EditableEntity<IdType> {
    fun getFields(): List<EntityField>
    fun copyWithFields(fields: Map<String, Any?>): EditableEntity<IdType>
}