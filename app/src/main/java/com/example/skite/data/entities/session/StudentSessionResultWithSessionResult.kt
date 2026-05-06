package com.example.skite.data.entities.session

import androidx.room.Embedded
import androidx.room.Relation

data class StudentSessionResultWithSessionResult(
    @Embedded
    val studentSessionResult: StudentSessionResult,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentSessionResultId"
    )
    val sessionResult: SessionResult
)