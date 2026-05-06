package com.example.skite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skite.data.converters.DateConverters
import com.example.skite.data.dao.*
import com.example.skite.data.entities.attendance.Attendance
import com.example.skite.data.entities.group.Group
import com.example.skite.data.entities.session.Session
import com.example.skite.data.entities.session.StudentSessionResult
import com.example.skite.data.entities.sessionType.SessionType
import com.example.skite.data.entities.resultType.ResultType
import com.example.skite.data.entities.session.SessionResult
import com.example.skite.data.entities.student.Student

@Database(
    entities = [
        Attendance::class, 
        Group::class, 
        StudentSessionResult::class,
        SessionResult::class,
        Session::class, 
        Student::class,
        SessionType::class,
        ResultType::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class SchoolDB : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao
    abstract fun groupDao(): GroupDao
    abstract fun studentSessionResultDao(): StudentSessionResultDao
    abstract fun sessionResultDao(): SessionResultDao
    abstract fun sessionDao(): SessionDao
    abstract fun studentDao(): StudentDao
    abstract fun sessionTypeDao(): SessionTypeDao
    abstract fun resultTypeDao(): ResultTypeDao
}
