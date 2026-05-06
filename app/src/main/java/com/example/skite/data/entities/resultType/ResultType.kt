package com.example.skite.data.entities.resultType

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skite.data.entities.enums.SessionTool
import com.example.skite.data.repositories.base.EntityWithId

@Entity(tableName = "Tresult_type")
data class ResultType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val toolName: String = SessionTool.NONE.value,
    val data: String = ""
) : EntityWithId<Int> {
    override fun entityId(): Int = id
}
