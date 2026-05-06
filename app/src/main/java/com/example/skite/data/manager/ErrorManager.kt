package com.example.skite.data.manager

import com.example.skite.data.error.AppError
import com.example.skite.utils.AndroidLogger
import javax.inject.Inject
import javax.inject.Singleton

enum class LogSeverity { DEBUG, INFO, WARN, ERROR }

interface ErrorManager {
    fun report(error: AppError, tag: String, severity: LogSeverity? = null)
    fun log(tag: String, message: String, severity: LogSeverity = LogSeverity.DEBUG)
}

@Singleton
class ErrorManagerImpl @Inject constructor() : ErrorManager {
    private val logger = AndroidLogger.getInstance()

    override fun report(error: AppError, tag: String, severity: LogSeverity?) {
        val finalSeverity = severity ?: error.defaultSeverity

        val message = "[${error.code}] ${error.message}"

        when (finalSeverity) {
            LogSeverity.DEBUG -> logger.debug(tag, message)
            LogSeverity.INFO -> logger.info(tag, message)
            LogSeverity.WARN -> logger.warn(tag, message)
            LogSeverity.ERROR -> error.cause?.let {
                logger.error(tag, message, it)
            } ?: logger.error(tag, message)
        }
    }

    override fun log(tag: String, message: String, severity: LogSeverity) {
        when (severity) {
            LogSeverity.DEBUG -> logger.debug(tag, message)
            LogSeverity.INFO -> logger.info(tag, message)
            LogSeverity.WARN -> logger.warn(tag, message)
            LogSeverity.ERROR -> logger.error(tag, message)
        }
    }
}
