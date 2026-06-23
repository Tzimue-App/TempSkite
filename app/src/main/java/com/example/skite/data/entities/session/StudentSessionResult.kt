package com.example.skite.data.entities.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.skite.data.entities.student.Student
import com.example.skite.data.repositories.base.EntityWithId

@Entity(
    tableName = "Tstudent_session_result",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["studentId", "sessionId"])
    ]
)
data class StudentSessionResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: Int,
    val sessionId: Int,
    val data: String = "",
    val updated: Boolean = false
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
