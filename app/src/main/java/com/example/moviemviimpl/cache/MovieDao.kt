package com.example.moviemviimpl.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviemviimpl.model.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie): Long

    @Query("SELECT * FROM movie")
    suspend fun getAllMoviesFromDB(): List<Movie>

    @Query(
        """
        SELECT * FROM movie
        ORDER BY title
    """
    )
    suspend fun getAllMoviesFromDBOrderedByTitle(): List<Movie>

    @Query(
        """
        SELECT * FROM movie
        ORDER BY releaseDate
    """
    )
    suspend fun getAllMoviesFromDBOrderedByYear(): List<Movie>
}