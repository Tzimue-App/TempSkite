package com.example.skite

import android.app.Application
import com.example.skite.data.db.DatabaseSeeder
import com.example.skite.utils.AndroidLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SkiteApp : Application() {

    @Inject lateinit var databaseSeeder: DatabaseSeeder
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val logger = AndroidLogger.getInstance()
    private val tag = "SkiteApp"

    override fun onCreate() {
        super.onCreate()
        seedDatabase()
        logger.debug(tag, "onCreate")
    }

    private fun seedDatabase() {
        applicationScope.launch {
            try {
                databaseSeeder.seedDatabase(true)
                logger.debug(tag, "Database seeded successfully")
            } catch (e: Exception) {
                logger.error(tag, "Database seeding failed: ${e.message}", e)
            }
        }
    }
}