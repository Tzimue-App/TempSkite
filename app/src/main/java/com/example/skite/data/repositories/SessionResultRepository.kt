package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.SessionResultDao
import com.example.skite.data.entities.session.SessionResult
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.example.skite.data.manager.ErrorManager
import javax.inject.Inject

class SessionResultRepository @Inject constructor(
    private val dao: SessionResultDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<SessionResult, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = SessionResult::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: SessionResult): SessionResult =
        entity.copy(id = id)

    suspend fun findAll(): DataResult<List<SessionResult>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<SessionResult>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<SessionResult> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<SessionResult>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }
}
