package com.example.moviemviimpl.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Convertors {
    @JvmStatic
    @TypeConverter
    fun toIntList(data: String): List<Int> {
        val gson = Gson()
        val listType = object : TypeToken<List<Int>>() {
        }.getType()
        return gson.fromJson(data, listType)
    }

    @JvmStatic
    @TypeConverter
    fun fromIntList(someObjects: List<Int>): String {
        val gson = Gson()
        return gson.toJson(someObjects)
    }
}