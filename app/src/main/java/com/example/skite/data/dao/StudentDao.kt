package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.student.Student
import com.example.skite.data.entities.student.StudentWithAttendances
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao : BaseDao<Student, Int> {

    @Query("SELECT * FROM Tstudent")
    suspend fun findAll(): List<Student>

    @Query("SELECT * FROM Tstudent")
    fun findAllFlow(): Flow<List<Student>>

    @Query("SELECT * FROM Tstudent WHERE id = :id")
    suspend fun findById(id: Int): Student?

    @Query("SELECT * FROM Tstudent WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<Student?>

    @Transaction
    @Query("SELECT * FROM Tstudent WHERE id = :studentId")
    suspend fun findWithAttendances(studentId: Int): StudentWithAttendances?

    @Transaction
    @Query("SELECT * FROM Tstudent WHERE id = :studentId")
    fun findWithAttendancesFlow(studentId: Int): Flow<StudentWithAttendances?>
}
