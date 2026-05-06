package com.example.skite.data.entities.student

import androidx.room.Embedded
import androidx.room.Relation
import com.example.skite.data.entities.attendance.Attendance

data class StudentWithAttendances(
    @Embedded
    val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    )
    val attendances: List<Attendance>
)
