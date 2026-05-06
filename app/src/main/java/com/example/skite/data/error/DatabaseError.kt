package com.example.skite.data.error

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.example.skite.data.manager.LogSeverity
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

sealed class DatabaseError(
    errorCode: String,
    message: String,
    uiMessage: String,
    cause: Throwable? = null,
    defaultSeverity: LogSeverity = LogSeverity.ERROR
) : AppError(errorCode, message, uiMessage, cause, defaultSeverity) {

    class EntityNotFound(id: String, entityName: String) :
        DatabaseError(
            errorCode = "DBE_ENTITY_NOT_FOUND",
            message = "$entityName with id $id not found",
            uiMessage = "Error 01",
            defaultSeverity = LogSeverity.WARN
        )

    class InsertionFailed(entityName: String, cause: Throwable?) :
        DatabaseError(
            errorCode = "DBE_INSERTION_FAILED",
            message = "Failed to insert ${entityName}: ${cause?.message}",
            uiMessage = "Error 02",
            cause
        )

    class BulkInsertionFailed(entityName: String, cause: Throwable?) :
        DatabaseError(
            errorCode = "DBE_BULK_INSERTION_FAILED",
            message = "Failed to insert multiple ${entityName}: ${cause?.message}",
            uiMessage = "Error 03",
            cause
        )

    class UpdateFailed(entityName: String, cause: Throwable?) :
        DatabaseError(
            errorCode = "DBE_UPDATE_FAILED",
            message = "Failed to update ${entityName}: ${cause?.message}",
            uiMessage = "Error 04",
            cause
        )

    class DeletionFailed(entityName: String, cause: Throwable?) :
        DatabaseError(
            errorCode = "DBE_DELETION_FAILED",
            message = "Failed to delete ${entityName}: ${cause?.message}",
            uiMessage = "Error 04",
            cause
        )

    class ConstraintViolationError(cause: Throwable) :
            DatabaseError(
                errorCode = "DBE_CONSTRAINT_VIOLATION_ERROR",
                message = "Database constraint violation: ${cause.message}",
                uiMessage = "Error 05",
                cause
            )

    class ConnectionError(cause: Throwable) :
        DatabaseError(
            errorCode = "DBE_CONNECTION_ERROR",
            message = "Database connection error: ${cause.message}",
            uiMessage = "Error 06",
            cause
        )

    class DataCorruptionError(cause: Throwable) :
        DatabaseError(
            errorCode = "DBE_DATA_CORRUPTION_ERROR",
            message = "Data corruption error: ${cause.message}",
            uiMessage = "Error 07",
            cause
        )

    class IllegalStateError(cause: Throwable) :
        DatabaseError(
            errorCode = "DBE_ILLEGAL_STATE_ERROR",
            message = "Illegal state error: ${cause.message}",
            uiMessage = "Error 08",
            cause
        )

    class UnknownError(cause: Throwable) :
        DatabaseError(
            errorCode = "DBE_UNKNOWN_ERROR",
            message = "An unknown error as occurred: ${cause.message}",
            uiMessage = "Error 09",
            cause
        )

    companion object {
        fun map(e: Throwable, fallback: (Throwable) -> DatabaseError): DatabaseError {
            return when (e) {
                is CancellationException -> throw e
                is SQLiteConstraintException -> ConstraintViolationError(e)
                is SQLiteException, is SocketTimeoutException -> ConnectionError(e)
                is SerializationException -> DataCorruptionError(e)
                is IllegalStateException -> IllegalStateError(e)
                else -> fallback(e)
            }
        }
    }
}
