package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.GroupDao
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.group.GroupFull
import com.example.skite.data.entities.group.GroupWithSessions
import com.example.skite.data.entities.group.GroupWithStudents
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.example.skite.data.manager.ErrorManager
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val dao: GroupDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<Group, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = Group::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: Group): Group = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<Group>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<Group>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<Group> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<Group>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }

    suspend fun findFull(id: Int): DataResult<GroupFull> =
        findSingle(id) { dao.findFull(id) }

    fun findFullFlow(id: Int): Flow<DataResult<GroupFull>> =
        findSingleFlow(id) { dao.findFullFlow(id) }

    suspend fun findWithSessions(id: Int): DataResult<GroupWithSessions> =
        findSingle(id) { dao.findWithSessions(id) }

    fun findWithSessionsFlow(id: Int): Flow<DataResult<GroupWithSessions>> =
        findSingleFlow(id) { dao.findWithSessionsFlow(id) }

    suspend fun findWithStudents(id: Int): DataResult<GroupWithStudents> =
        findSingle(id) { dao.findWithStudents(id) }

    fun findWithStudentsFlow(id: Int): Flow<DataResult<GroupWithStudents>> =
        findSingleFlow(id) { dao.findWithStudentsFlow(id) }
}
