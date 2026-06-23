package com.example.skite.data.entities.attendance

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.base.EntityWithId

@Entity(
    tableName = "Tattendance",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["sessionId","studentId"])
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sessionId: Int,
    val studentId: Int,
    val attendance: SessionAttendance
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
