package com.example.skite.data.error

sealed class CacheError(code: String, message: String) : AppError(code, message, "") {
    class Expired(key: String) : CacheError("CACHE_EXPIRED", "Cache entry for key $key has expired")
}
