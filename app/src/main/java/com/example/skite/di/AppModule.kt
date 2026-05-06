package com.example.skite.di

import android.content.Context
import androidx.room.Room
import com.example.skite.config.CacheConfig
import com.example.skite.data.dao.*
import com.example.skite.data.db.SchoolDB
import com.example.skite.data.repositories.*
import com.example.skite.data.converters.DateConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSchoolDB(
        @ApplicationContext appContext: Context,
        dateConverters: DateConverters
    ): SchoolDB {
        return Room.databaseBuilder(
            appContext,
            SchoolDB::class.java,
            "school_db"
        )
        .addTypeConverter(dateConverters)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideGroupDao(db: SchoolDB): GroupDao = db.groupDao()

    @Provides
    fun provideStudentDao(db: SchoolDB): StudentDao = db.studentDao()

    @Provides
    fun provideSessionDao(db: SchoolDB): SessionDao = db.sessionDao()

    @Provides
    fun provideAttendanceDao(db: SchoolDB): AttendanceDao = db.attendanceDao()

    @Provides
    fun provideStudentSessionResultDao(db: SchoolDB): StudentSessionResultDao = db.studentSessionResultDao()

    @Provides
    fun provideSessionResultDao(db: SchoolDB): SessionResultDao= db.sessionResultDao()

    @Provides
    fun provideSessionTypeDao(db: SchoolDB): SessionTypeDao = db.sessionTypeDao()

    @Provides
    fun provideResultTypeDao(db: SchoolDB): ResultTypeDao = db.resultTypeDao()
}
