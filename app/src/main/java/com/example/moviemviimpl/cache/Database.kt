package com.example.moviemviimpl.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.utils.Convertors

@TypeConverters(Convertors::class)
@Database(entities = [Movie::class], version = 4)
abstract class Database : RoomDatabase() {

    abstract fun getMoviesDao(): MovieDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }

}