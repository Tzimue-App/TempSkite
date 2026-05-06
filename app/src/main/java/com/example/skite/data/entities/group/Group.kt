package com.example.skite.data.entities.group

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.ui.common.classComponent.EditableEntity
import com.example.skite.ui.common.classComponent.EntityField
import com.example.skite.ui.common.classComponent.FieldType

@Entity(tableName = "Tgroup")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val desc: String = "",
    val year: Int = 0
) : EntityWithId<Int>, EditableEntity<Int> {
    override fun entityId(): Int = id

    override fun getFields(): List<EntityField> {
        return listOf(
            EntityField("name", "Name", name, FieldType.TEXT),
            EntityField("desc", "Description", desc, FieldType.TEXT),
            EntityField("year", "Year", year, FieldType.NUMBER)
        )
    }

    override fun copyWithFields(fields: Map<String, Any?>): Group {
        return this.copy(
            name = fields["name"]?.toString() ?: name,
            desc = fields["desc"]?.toString() ?: desc,
            year = fields["year"]?.toString()?.toIntOrNull() ?: year
        )
    }
}
