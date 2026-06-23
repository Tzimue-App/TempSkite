package com.example.skite.data.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.skite.data.entities.enums.SessionTool
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: SchoolDB,
    @ApplicationContext private val context: Context
) {
    private val TAG = "DatabaseSeeder"
    private val PREF_KEY_DB_SEEDED = "DB_SEEDED_V4"
    private val prefs: SharedPreferences = context.getSharedPreferences("database_seeder_prefs", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun seedDatabase(forceReseed: Boolean) = withContext(Dispatchers.IO) {
        if (forceReseed || !prefs.contains(PREF_KEY_DB_SEEDED)) {
            Log.d(TAG, "Seeding database...")
            seedGroups()
            seedStudents()
            seedResultTypes()
            seedSessionTypes()
            seedSessions()
            
            prefs.edit().putBoolean(PREF_KEY_DB_SEEDED, true).apply()
            Log.d(TAG, "Database seeded.")
        } else {
            Log.d(TAG, "Database already seeded.")
        }
    }

    fun resetSeedStatus() {
        prefs.edit().remove(PREF_KEY_DB_SEEDED).apply()
        Log.d(TAG, "Seed status reset.")
    }

    private suspend fun seedGroups() {
        if (database.groupDao().findAll().isNotEmpty()) return
        val groupData = listOf(
            Triple("Lundi 3-4", 6, "6 AC"),
            Triple("Lundi 5-6", 5, "5 CD"),
            Triple("Mercredi 1-2", 4, "4 AF"),
            Triple("Mercredi 3-4", 4, "4 BE"),
            Triple("Jeudi 3-4", 4, "4 CD"),
            Triple("Vendredi 1-2", 6, "6 BD"),
            Triple("Vendredi 3-4", 5, "5 B")
        )
        val groups = groupData.map { (name, year, desc) ->
            com.example.skite.data.entities.group.Group(name = name, desc = desc, year = year)
        }
        groups.forEach { database.groupDao().add(it) }
    }

    private suspend fun seedStudents(groupIds: Map<String, Int> = emptyMap()) {
        if (database.studentDao().findAll().isNotEmpty()) return
        val groups = database.groupDao().findAll()
        val students = groups.flatMap { group ->
            (1..10).map { i ->
                com.example.skite.data.entities.student.Student(
                    name = "Student $i of ${group.name}",
                    groupId = group.id,
                    number = i
                )
            }
        }
        students.forEach { database.studentDao().add(it) }
    }

    private suspend fun seedResultTypes() {
        if (database.resultTypeDao().findAll().isNotEmpty()) return
        
        val tjSkills = """{"skills":[{"name":"TJ","ratio":1.0}]}"""
        val volleySkills = """{"skills":[{"name":"Skill One","ratio":0.5},{"name":"Skill Two","ratio":0.2},{"name":"Skill Three","ratio":0.3}]}"""
        
        val tjResultType = com.example.skite.data.entities.resultType.ResultType(
            name = "TJ",
            toolName = SessionTool.NONE,
            data = tjSkills
        )
        val volleyResultType = com.example.skite.data.entities.resultType.ResultType(
            name = "volley Test",
            toolName = SessionTool.NONE,
            data = volleySkills
        )
        database.resultTypeDao().add(tjResultType)
        database.resultTypeDao().add(volleyResultType)
    }

    private suspend fun seedSessionTypes() {
        if (database.sessionTypeDao().findAll().isNotEmpty()) return
        
        val resultTypes = database.resultTypeDao().findAll()
        val tjResultTypeId = resultTypes.find { it.name == "TJ" }?.id ?: return
        val volleyResultTypeId = resultTypes.find { it.name == "volley Test" }?.id ?: return
        
        val tjVolleySessionType = com.example.skite.data.entities.sessionType.SessionType(
            name = "TJ Volley",
            resultTypeId = tjResultTypeId,
            tool = SessionTool.NONE
        )
        val testVolleySessionType = com.example.skite.data.entities.sessionType.SessionType(
            name = "Test Volley",
            resultTypeId = volleyResultTypeId,
            tool = SessionTool.NONE
        )
        
        database.sessionTypeDao().add(tjVolleySessionType)
        database.sessionTypeDao().add(testVolleySessionType)
    }

    private suspend fun seedSessions(groupIds: Map<String, Int> = emptyMap()) {
        if (database.sessionDao().findAll().isNotEmpty()) return
        val groups = database.groupDao().findAll()
        val sessionTypes = database.sessionTypeDao().findAll()
        val tjVolleySessionTypeId = sessionTypes.find { it.name == "TJ Volley" }?.id
        val testVolleySessionTypeId = sessionTypes.find { it.name == "Test Volley" }?.id
        
        if (groups.isEmpty()) return
        
        val sessionsToCreate = mutableListOf<com.example.skite.data.entities.session.Session>()
        
        groups.forEachIndexed { index, group ->
            if (index == 0) {
                for (i in 1..5) {
                    sessionsToCreate.add(
                        com.example.skite.data.entities.session.Session(
                            groupId = group.id,
                            date = java.util.Date(),
                            state = com.example.skite.data.entities.enums.SessionState.PLANNED,
                            name = "volley $i",
                            sessionTypeId = tjVolleySessionTypeId
                        )
                    )
                }
                sessionsToCreate.add(
                    com.example.skite.data.entities.session.Session(
                        groupId = group.id,
                        date = java.util.Date(),
                        state = com.example.skite.data.entities.enums.SessionState.PLANNED,
                        name = "Perf Volley",
                        sessionTypeId = testVolleySessionTypeId
                    )
                )
            } else {
                for (i in 1..3) {
                    sessionsToCreate.add(
                        com.example.skite.data.entities.session.Session(
                            groupId = group.id,
                            date = java.util.Date(),
                            state = com.example.skite.data.entities.enums.SessionState.PLANNED,
                            name = "Session $i"
                        )
                    )
                }
            }
        }
        
        sessionsToCreate.forEach { database.sessionDao().add(it) }
    }
    
    private suspend fun seedAttendance() {
        // Basic attendance seeding if needed, or leave empty for now
    }
}
