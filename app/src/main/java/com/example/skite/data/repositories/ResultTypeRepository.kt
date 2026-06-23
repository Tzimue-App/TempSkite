package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.ResultTypeDao
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.repositories.base.BaseRepository
import com.example.skite.data.result.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResultTypeRepository @Inject constructor(
    private val dao: ResultTypeDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<ResultType, Int>(
    dao,
    config,
    errorManager,
    entityClass = ResultType::class
) {
    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: ResultType): ResultType = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<ResultType>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<ResultType>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<ResultType> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<ResultType>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }
}
