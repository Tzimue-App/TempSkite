package com.example.skite.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.skite.data.dao.base.BaseDao
import com.example.skite.data.entities.resultType.ResultType
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultTypeDao : BaseDao<ResultType, Int> {

    @Query("SELECT * FROM Tresult_type")
    suspend fun findAll(): List<ResultType>

    @Query("SELECT * FROM Tresult_type")
    fun findAllFlow(): Flow<List<ResultType>>

    @Query("SELECT * FROM Tresult_type WHERE id = :id")
    suspend fun findById(id: Int): ResultType?

    @Query("SELECT * FROM Tresult_type WHERE id = :id")
    fun findByIdFlow(id: Int): Flow<ResultType?>


}
