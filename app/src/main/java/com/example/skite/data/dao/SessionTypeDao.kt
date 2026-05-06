package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.sessionType.SessionType
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionTypeDao : BaseDao<SessionType, Int> {

    @Query("SELECT * FROM Tsession_type")
    suspend fun findAll(): List<SessionType>

    @Query("SELECT * FROM Tsession_type")
    fun findAllFlow(): Flow<List<SessionType>>

    @Query("SELECT * FROM Tsession_type WHERE id = :id")
    suspend fun findById(id: Int): SessionType?

    @Query("SELECT * FROM Tsession_type WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<SessionType?>
}
