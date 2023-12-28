package com.free.momobooklet_by_sm.data.local.converters

import androidx.room.TypeConverter
import java.sql.Timestamp

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.time
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }
}