package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.session.SessionWithAttendances
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao : BaseDao<Session, Int> {

    @Query("SELECT * FROM Tsession")
    suspend fun findAll(): List<Session>

    @Query("SELECT * FROM Tsession")
    fun findAllFlow(): Flow<List<Session>>

    @Query("SELECT * FROM Tsession WHERE id = :id")
    suspend fun findById(id: Int): Session?

    @Query("SELECT * FROM Tsession WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<Session?>

    @Transaction
    @Query("SELECT * FROM Tsession WHERE id = :sessionId")
    suspend fun findWithAttendances(sessionId: Int): SessionWithAttendances?

    @Transaction
    @Query("SELECT * FROM Tsession WHERE id = :sessionId")
    fun findWithAttendancesFlow(sessionId: Int): Flow<SessionWithAttendances?>
}
