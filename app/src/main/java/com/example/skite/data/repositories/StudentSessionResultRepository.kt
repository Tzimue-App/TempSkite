package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.StudentSessionResultDao
import com.example.skite.data.entities.session.SessionWithAttendances
import com.example.skite.data.entities.session.StudentSessionResult
import com.example.skite.data.entities.session.StudentSessionResultWithSessionResult
import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.repositories.base.BaseRepository
import com.example.skite.data.result.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentSessionResultRepository @Inject constructor(
    private val dao: StudentSessionResultDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<StudentSessionResult, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = StudentSessionResult::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: StudentSessionResult): StudentSessionResult = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<StudentSessionResult>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<StudentSessionResult>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<StudentSessionResult> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<StudentSessionResult>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }
        
    fun findBySessionIdFlow(sessionId: Int): Flow<DataResult<List<StudentSessionResult>>> =
        findAllFlow { dao.findBySessionIdFlow(sessionId) }

    suspend fun findWithSessionResult(id: Int): DataResult<StudentSessionResultWithSessionResult> =
        findSingle(id = id) { dao.findWithSessionResult(id) }

    fun findWithSessionResultFlow(id: Int): Flow<DataResult<StudentSessionResultWithSessionResult>> =
        findSingleFlow(id = id) { dao.findWithSessionResultFlow(id) }
}
