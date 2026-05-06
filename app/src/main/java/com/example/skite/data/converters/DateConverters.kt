package com.example.skite.data.converters

import androidx.room.TypeConverter
import androidx.room.ProvidedTypeConverter
import com.example.skite.data.entities.enums.SessionAttendance
import com.example.skite.data.entities.enums.SessionSportType
import com.example.skite.data.entities.enums.SessionState
import com.example.skite.data.error.ConversionError
import com.example.skite.data.manager.ErrorManager
import com.example.skite.data.manager.LogSeverity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

//TODO Clean converters
@ProvidedTypeConverter
class DateConverters @Inject constructor(
    private val errorManager: ErrorManager
) {
    private val tag = "DateConverters"
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @TypeConverter
    fun fromString(value: String?): Date? {
        return value?.let {
            try {
                formatter.parse(it)
            } catch (e: Exception) {
                errorManager.report(ConversionError("Date string: $it", e), tag, LogSeverity.WARN)
                null
            }
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let { formatter.format(it) }
    }

    @TypeConverter
    fun fromSessionState(value: SessionState): String = value.name

    @TypeConverter
    fun toSessionState(value: String): SessionState =
        SessionState.valueOf(value)

    @TypeConverter
    fun fromSessionSportType(value: SessionSportType): String = value.name

    @TypeConverter
    fun toSessionSportType(value: String): SessionSportType =
        SessionSportType.valueOf(value)

    @TypeConverter
    fun fromSessionAttendance(value: SessionAttendance): String = value.name

    @TypeConverter
    fun toSessionAttendance(value: String): SessionAttendance =
        SessionAttendance.valueOf(value)
}
