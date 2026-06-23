package com.example.skite.data.entities.sessionType

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.data.entities.resultType.ResultType

@Entity(
    tableName = "Tsession_type",
    foreignKeys = [
        ForeignKey(
            entity = ResultType::class,
            parentColumns = ["id"],
            childColumns = ["resultTypeId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["resultTypeId"])
    ]
)
data class SessionType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val resultTypeId: Int = 0,
    val tool: SessionTool = SessionTool.NONE,
    val defaultGradeDisplay: Int = 100,
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
