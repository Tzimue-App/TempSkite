package com.example.skite.di

import com.example.skite.config.AppCacheConfig
import com.example.skite.config.CacheConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideCacheConfig(): CacheConfig {
        return AppCacheConfig()
    }
}
