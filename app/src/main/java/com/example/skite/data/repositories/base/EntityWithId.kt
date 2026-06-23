package com.example.skite.data.repositories.base

interface EntityWithId<ID> {
    fun entityId(): ID
}
