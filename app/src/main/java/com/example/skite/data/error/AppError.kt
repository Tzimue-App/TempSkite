package com.example.skite.data.error

import com.example.skite.data.manager.LogSeverity

sealed class AppError(
    val code: String, 
    val message: String,
    val uiMessage: String,
    val cause: Throwable? = null,
    val defaultSeverity: LogSeverity = LogSeverity.ERROR
)
