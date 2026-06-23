package com.example.skite.data.dao.base

import androidx.room.*

interface BaseDao<T, ID> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun add(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addAll(entities: List<T>): List<Long>

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int
}
