package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.session.StudentSessionResult
import com.example.skite.data.entities.session.StudentSessionResultWithSessionResult
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSessionResultDao : BaseDao<StudentSessionResult, Int> {

    @Query("SELECT * FROM Tstudent_session_result")
    suspend fun findAll(): List<StudentSessionResult>

    @Query("SELECT * FROM Tstudent_session_result")
    fun findAllFlow(): Flow<List<StudentSessionResult>>

    @Query("SELECT * FROM Tstudent_session_result WHERE id = :id")
    suspend fun findById(id: Int): StudentSessionResult?

    @Query("SELECT * FROM Tstudent_session_result WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<StudentSessionResult?>
    
    @Query("SELECT * FROM Tstudent_session_result WHERE sessionId = :sessionId")
    fun findBySessionIdFlow(sessionId: Int): Flow<List<StudentSessionResult>>

    @Transaction
    @Query("SELECT * FROM Tstudent_session_result WHERE id = :studentSessionId")
    suspend fun findWithSessionResult(studentSessionId: Int): StudentSessionResultWithSessionResult?

    @Transaction
    @Query("SELECT * FROM Tstudent_session_result WHERE id = :studentSessionId")
    fun findWithSessionResultFlow(studentSessionId: Int): Flow<StudentSessionResultWithSessionResult?>
}
