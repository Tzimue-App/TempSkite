package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.session.SessionResult
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionResultDao : BaseDao<SessionResult, Int> {

    @Query("SELECT * FROM Tresult")
    suspend fun findAll(): List<SessionResult>

    @Query("SELECT * FROM Tresult")
    fun findAllFlow(): Flow<List<SessionResult>>

    @Query("SELECT * FROM Tresult WHERE id = :id")
    suspend fun findById(id: Int): SessionResult?

    @Query("SELECT * FROM Tresult WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<SessionResult?>
}
