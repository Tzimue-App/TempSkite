package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.SessionTypeDao
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.repositories.base.BaseRepository
import com.example.skite.data.result.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionTypeRepository @Inject constructor(
    private val dao: SessionTypeDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<SessionType, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = SessionType::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: SessionType): SessionType = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<SessionType>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<SessionType>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<SessionType> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<SessionType>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }
}
