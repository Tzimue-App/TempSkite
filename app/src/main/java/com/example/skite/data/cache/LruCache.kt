package com.example.skite.data.cache

import com.example.skite.data.error.CacheError
import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.manager.LogSeverity

class LruCache<K, V>(
    private val maxSize: Int,
    private val errorManager: ErrorManager? = null
) {
    private val tag = "LruCache"
    private val map = object : LinkedHashMap<K, CacheEntry<V>>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, CacheEntry<V>>?): Boolean {
            return size > maxSize
        }
    }

    private data class CacheEntry<V>(
        val value: V,
        val expirationTime: Long
    ) {
        fun isExpired(): Boolean {
            return expirationTime > 0 && System.currentTimeMillis() > expirationTime
        }
    }

    @Synchronized
    fun get(key: K): V? {
        val entry = map[key] ?: return null
        if (entry.isExpired()) {
            errorManager?.report(CacheError.Expired(key.toString()), tag, LogSeverity.DEBUG)
            map.remove(key)
            return null
        }
        return entry.value
    }

    @Synchronized
    fun put(key: K, value: V, ttlMillis: Long) {
        val expirationTime = if (ttlMillis > 0) System.currentTimeMillis() + ttlMillis else 0
        map[key] = CacheEntry(value, expirationTime)
    }

    @Synchronized
    fun remove(key: K): V? {
        return map.remove(key)?.value
    }

    @Synchronized
    fun clear() {
        map.clear()
    }

    @Synchronized
    fun size(): Int {
        return map.size
    }

    @Synchronized
    fun removeExpiredEntries() {
        val currentTime = System.currentTimeMillis()
        val iterator = map.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.expirationTime in 1 until currentTime) {
                iterator.remove()
            }
        }
    }
}
