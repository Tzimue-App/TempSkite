package com.example.skite.data.entities.sessionType

import androidx.room.Embedded
import androidx.room.Relation
import com.example.skite.data.entities.resultType.ResultType

data class SessionTypeWithResultType(
    @Embedded
    val sessionType: SessionType,
    @Relation(
        parentColumn = "resultTypeId",
        entityColumn = "id"
    )
    val resultType: ResultType?
)
