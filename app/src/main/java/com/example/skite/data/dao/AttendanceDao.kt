package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.attendance.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao : BaseDao<Attendance, Int> {

    @Query("SELECT * FROM Tattendance")
    suspend fun findAll(): List<Attendance>

    @Query("SELECT * FROM Tattendance")
    fun findAllFlow(): Flow<List<Attendance>>

    @Query("SELECT * FROM Tattendance WHERE id = :id")
    suspend fun findById(id: Int): Attendance?

    @Query("SELECT * FROM Tattendance WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<Attendance?>
}
