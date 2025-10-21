package com.daycheck.app.data.models

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database to handle LocalDate and LocalDateTime serialization.
 * Room doesn't natively support Java 8 time classes, so we convert them to/from strings.
 */
class Converters {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }
}