package com.example.skite.data.entities.group

import androidx.room.Embedded
import androidx.room.Relation
import com.example.skite.data.entities.student.Student

data class GroupWithStudents(
    @Embedded
    val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val students: List<Student>
)
