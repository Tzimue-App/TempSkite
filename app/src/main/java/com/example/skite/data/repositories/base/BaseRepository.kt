package com.example.skite.data.repositories.base

import com.example.skite.data.dao.base.BaseDao
import com.example.skite.config.CacheConfig
import com.example.skite.data.cache.LruCache
import com.example.skite.data.result.DataResult
import com.example.skite.data.error.DatabaseError
import com.example.skite.data.manager.ErrorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

abstract class BaseRepository<E : EntityWithId<ID>, ID : Any>(
    private val dao: BaseDao<E, ID>,
    config: CacheConfig,
    private val errorManager: ErrorManager,
    entityClass: KClass<E>
) {
    private val cache = LruCache<ID, E>(config.cacheSize, errorManager)
    private val ttlMillis = config.cacheTtlMillis

    private val repositoryName: String = this::class.simpleName ?: "UnknownRepository"
    private val entityName: String = entityClass.simpleName ?: "UnknownEntity"

    protected abstract fun convertToIdType(id: Long): ID
    protected abstract fun createCopy(id: ID, entity: E): E

    suspend fun add(entity: E): DataResult<Long> = safeDbCall(
        validate = { id ->
            if (id == -1L) DatabaseError.InsertionFailed(entityName, null) else null
        }
    ) {
        dao.add(entity)
    }.onSuccess { generatedId ->
        val idTyped = convertToIdType(generatedId)
        cache.put(idTyped, createCopy(idTyped, entity), ttlMillis)
    }

    suspend fun addAll(entities: List<E>): DataResult<List<Long>> = safeDbCall(
        validate = { ids ->
            val failedCount = ids.count { it == -1L }
            if (failedCount > 0) DatabaseError.BulkInsertionFailed(entityName, null) else null
        }
    ) {
        dao.addAll(entities)
    }.onSuccess { ids ->
        entities.zip(ids).forEach { (entity, id) ->
            if (id != -1L) {
                val idTyped = convertToIdType(id)
                cache.put(idTyped, createCopy(idTyped, entity), ttlMillis)
            }
        }
    }

    suspend fun update(entity: E): DataResult<Int> {
        cache.remove(entity.entityId())
        return safeDbCall(
            validate = { rows ->
                if (rows == 0) DatabaseError.UpdateFailed(entityName, null) else null
            }
        ) {
            dao.update(entity)
        }.onSuccess {
            cache.put(entity.entityId(), entity, ttlMillis)
        }
    }

    suspend fun delete(entity: E): DataResult<Int> = safeDbCall(
        validate = { rows ->
            if (rows == 0) DatabaseError.DeletionFailed(entityName, null) else null
        }
    ) {
        dao.delete(entity)
    }.onSuccess {
        cache.remove(entity.entityId())
    }

    protected suspend fun <T> findAll(
        block: suspend () -> T
    ): DataResult<T> = safeDbCall { block() }

    protected fun <T> findAllFlow(
        block: () -> Flow<List<T>>
    ): Flow<DataResult<List<T>>> = block().safeFlowList()

    protected suspend fun findSingleCached(
        id: ID,
        block: suspend () -> E?
    ): DataResult<E> {
        val cached = cache.get(id)
        if (cached != null) return DataResult.Success(cached)

        return findSingle(id, block).onSuccess { entity ->
            cache.put(id, createCopy(id, entity), ttlMillis)
        }
    }

    protected suspend fun <T> findSingle(
        id: ID,
        block: suspend () -> T?
    ): DataResult<T> = safeDbCall {
        block()
    }.flatMap { result ->
        if (result == null) {
            val error = DatabaseError.EntityNotFound(id.toString(), entityName)
            errorManager.report(error, repositoryName)
            DataResult.Error(error)
        } else {
            DataResult.Success(result)
        }
    }

    protected fun <T> findSingleFlow(
        id: ID,
        block: () -> Flow<T?>
    ): Flow<DataResult<T>> = block().safeFlow(id)

    @PublishedApi
    internal suspend fun <T> safeDbCall(
        validate: ((T) -> DatabaseError?)? = null,
        block: suspend () -> T
    ): DataResult<T> {
        return try {
            val result = block()
            val validationError = validate?.invoke(result)
            if (validationError != null) {
                errorManager.report(validationError, repositoryName)
                DataResult.Error(validationError)
            } else {
                DataResult.Success(result)
            }
        } catch (e: Exception) {
            val error = DatabaseError.map(e) { DatabaseError.UnknownError(it) }
            errorManager.report(error, repositoryName)
            DataResult.Error(error)
        }
    }

    @PublishedApi
    internal fun <T> Flow<T?>.safeFlow(
        id: ID
    ): Flow<DataResult<T>> =
        map<T?, DataResult<T>> { data ->
            if (data != null) {
                DataResult.Success(data)
            } else {
                val error = DatabaseError.EntityNotFound(id.toString(), entityName)
                errorManager.report(error, repositoryName)
                DataResult.Error(error)
            }
        }.catch { e ->
            val error = DatabaseError.map(e) { DatabaseError.UnknownError(it) }
            errorManager.report(error, repositoryName)
            emit(DataResult.Error(error))
        }

    @PublishedApi
    internal fun <T> Flow<List<T>>.safeFlowList(
    ): Flow<DataResult<List<T>>> =
        map<List<T>, DataResult<List<T>>> { list ->
            DataResult.Success(list)
        }.catch { e ->
            val error = DatabaseError.map(e) { DatabaseError.UnknownError(it) }
            errorManager.report(error, repositoryName)
            emit(DataResult.Error(error))
        }

    fun clearCache() {
        cache.clear()
    }
}