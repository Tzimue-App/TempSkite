package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.SessionDao
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.session.SessionWithAttendances
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.example.skite.data.manager.ErrorManager
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val dao: SessionDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<Session, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = Session::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: Session): Session = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<Session>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<Session>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<Session> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<Session>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }

    suspend fun findWithAttendances(id: Int): DataResult<SessionWithAttendances> =
        findSingle(id = id) { dao.findWithAttendances(id) }

    fun findWithAttendancesFlow(id: Int): Flow<DataResult<SessionWithAttendances>> =
        findSingleFlow(id = id) { dao.findWithAttendancesFlow(id) }
}
