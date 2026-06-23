package com.example.skite.data.entities.session

import androidx.room.Embedded
import androidx.room.Relation
import com.example.skite.data.entities.attendance.Attendance

data class SessionWithAttendances(
    @Embedded
    val session: Session,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val attendances: List<Attendance>
)
