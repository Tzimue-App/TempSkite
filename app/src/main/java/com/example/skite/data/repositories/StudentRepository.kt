package com.example.skite.data.repositories

import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.StudentDao
import com.example.skite.data.entities.student.Student
import com.example.skite.data.entities.student.StudentWithAttendances
import com.example.skite.data.result.DataResult
import com.example.skite.data.repositories.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.example.skite.data.manager.ErrorManager
import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val dao: StudentDao,
    config: CacheConfig,
    errorManager: ErrorManager
) : BaseRepository<Student, Int>(
    dao = dao,
    config,
    errorManager,
    entityClass = Student::class
) {

    override fun convertToIdType(id: Long): Int = id.toInt()

    override fun createCopy(id: Int, entity: Student): Student = entity.copy(id = id)

    suspend fun findAll(): DataResult<List<Student>> =
        findAll { dao.findAll() }

    fun findAllFlow(): Flow<DataResult<List<Student>>> =
        findAllFlow { dao.findAllFlow() }

    suspend fun findById(id: Int): DataResult<Student> =
        findSingleCached(id) { dao.findById(id) }

    fun findByIdFlow(id: Int): Flow<DataResult<Student>> =
        findSingleFlow(id) { dao.findByIdFlow(id) }

    suspend fun findWithAttendances(id: Int): DataResult<StudentWithAttendances> =
        findSingle(id = id) { dao.findWithAttendances(id) }

    fun findWithAttendancesFlow(id: Int): Flow<DataResult<StudentWithAttendances>> =
        findSingleFlow(id = id) { dao.findWithAttendancesFlow(id) }
}
