package com.example.skite.data.entities.group

import androidx.room.Embedded
import androidx.room.Relation
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.student.Student

data class GroupFull(
    @Embedded
    val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val students: List<Student>,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val sessions: List<Session>
)
