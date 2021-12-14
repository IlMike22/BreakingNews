package com.androiddevs.mvvmnewsapp.data.remote.database

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.data.remote.api.dto.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source): String = source.name

    @TypeConverter
    fun toSource(name: String): Source = Source(name = name, id = name)
}