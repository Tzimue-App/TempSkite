package com.example.skite.data.entities.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.repositories.base.EntityWithId

@Entity(
    tableName = "Tresult",
    foreignKeys = [
        ForeignKey(
            entity = StudentSessionResult::class,
            parentColumns = ["id"],
            childColumns = ["studentSessionResultId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["studentSessionResultId"])
    ]
)
data class SessionResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentSessionResultId: Int,
    val grade: Float,
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
