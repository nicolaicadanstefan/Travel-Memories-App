package com.stefandev.travelmemories.data

import androidx.room.TypeConverter
import java.util.Date

// Because Room doesn't support date type, we need to convert from Date -> Long and vice versa
class DateTypeConverter {
    // From Long->Date
    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }
    // From Date->Long
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}