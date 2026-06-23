package com.example.skite.config

import javax.inject.Inject

class AppCacheConfig @Inject constructor() : CacheConfig {
    override val cacheSize: Int = 20
    override val cacheTtlMillis: Long = 600000 // 10 minutes
}
