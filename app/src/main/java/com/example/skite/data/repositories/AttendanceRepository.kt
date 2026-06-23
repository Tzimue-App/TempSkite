package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.AttendanceDao
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.example.skite.data.manager.ErrorManager
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val dao: AttendanceDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<Attendance, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = Attendance::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: Attendance): Attendance = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<Attendance>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<Attendance>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<Attendance> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<Attendance>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }
}
