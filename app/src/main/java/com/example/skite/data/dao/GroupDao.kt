package com.example.skite.data.dao

import androidx.room.*
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.group.GroupFull
import com.example.skite.data.entities.group.GroupWithSessions
import com.example.skite.data.entities.group.GroupWithStudents
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao : BaseDao<Group, Int> {

    @Query("SELECT * FROM Tgroup")
    suspend fun findAll(): List<Group>

    @Query("SELECT * FROM Tgroup")
    fun findAllFlow(): Flow<List<Group>>

    @Query("SELECT * FROM Tgroup WHERE id = :id")
    suspend fun findById(id: Int): Group?

    @Query("SELECT * FROM Tgroup WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<Group?>

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    suspend fun findFull(groupId: Int): GroupFull?

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    fun findFullFlow(groupId: Int): Flow<GroupFull?>

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    suspend fun findWithSessions(groupId: Int): GroupWithSessions?

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    fun findWithSessionsFlow(groupId: Int): Flow<GroupWithSessions?>

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    suspend fun findWithStudents(groupId: Int): GroupWithStudents?

    @Transaction
    @Query("SELECT * FROM Tgroup WHERE id = :groupId")
    fun findWithStudentsFlow(groupId: Int): Flow<GroupWithStudents?>
}
