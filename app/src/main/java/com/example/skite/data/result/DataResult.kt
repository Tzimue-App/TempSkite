package com.example.skite.data.result

import com.example.skite.data.error.AppError

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val error: AppError) : DataResult<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun errorOrNull(): AppError? = when (this) {
        is Success -> null
        is Error -> error
    }

    inline fun <R> fold(onSuccess: (T) -> R, onError: (AppError) -> R): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(error)
        }
    }

    inline fun <R> map(transform: (T) -> R): DataResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error)
        }
    }

    inline fun <R> flatMap(transform: (T) -> DataResult<R>): DataResult<R> {
        return when (this) {
            is Success -> transform(data)
            is Error -> Error(error)
        }
    }

    inline fun onSuccess(action: (T) -> Unit): DataResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppError) -> Unit): DataResult<T> {
        if (this is Error) action(error)
        return this
    }
}
