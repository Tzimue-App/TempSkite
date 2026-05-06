package com.example.skite.di

import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.manager.ErrorManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {

    @Binds
    @Singleton
    abstract fun bindErrorManager(
        errorManagerImpl: ErrorManagerImpl
    ): ErrorManager
}
