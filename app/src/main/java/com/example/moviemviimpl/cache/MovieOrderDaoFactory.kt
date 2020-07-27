package com.example.moviemviimpl.cache

import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.utils.Constants


suspend fun MovieDao.returnOrderedMovies(
    order: String
): List<Movie> {
    return when (order) {
        Constants.ORDER_BY_TITLE -> {
            getAllMoviesFromDBOrderedByTitle()
        }

        Constants.ORDER_BY_YEAR -> {
            getAllMoviesFromDBOrderedByYear()
        }
        else -> {
            getAllMoviesFromDBOrderedByTitle()
        }
    }
}