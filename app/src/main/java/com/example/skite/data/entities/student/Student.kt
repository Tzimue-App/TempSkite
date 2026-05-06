package com.example.skite.data.entities.student

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.entities.group.Group
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.ui.common.classComponent.EditableEntity
import com.example.skite.ui.common.classComponent.EntityField
import com.example.skite.ui.common.classComponent.FieldType

@Entity(
    tableName = "Tstudent",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["groupId"])
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val number: Int,
    val groupId: Int
) : EntityWithId<Int>, EditableEntity<Int> {
    override fun entityId(): Int = id

    override fun getFields(): List<EntityField> {
        return listOf(
            EntityField("name", "Name", name, FieldType.TEXT),
            EntityField("number", "Number", number, FieldType.NUMBER),
            EntityField("groupId", "GroupId", groupId, FieldType.NUMBER)
        )
    }

    override fun copyWithFields(fields: Map<String, Any?>): Student {
        return this.copy(
            name = fields["name"]?.toString() ?: name,
            number = fields["number"]?.toString()?.toIntOrNull() ?: number,
            groupId = fields["groupId"]?.toString()?.toIntOrNull() ?: groupId
        )
    }
}
