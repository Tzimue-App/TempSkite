package com.example.skite.data.entities.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.repositories.base.EntityWithId
import com.example.skite.data.entities.enums.SessionSportType
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.sessionType.SessionType
import java.util.Date

@Entity(
    tableName = "Tsession",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SessionType::class,
            parentColumns = ["id"],
            childColumns = ["sessionTypeId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["groupId", "sessionTypeId"])
    ]
)
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: Date?,
    val groupId: Int,
    val sessionTypeId: Int? = null,
    val sportType: SessionSportType = SessionSportType.RUNNING,
    val state: SessionState = SessionState.PLANNED
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
