package com.example.moviemviimpl.state

import com.example.moviemviimpl.model.Movie

data class MainScreenViewState(


    val moviesFields: MoviesFields = MoviesFields()

//    var movies: List<Movie>? = null
)


data class MoviesFields(
    var movies: List<Movie>? = null,
    var order: String? = null
)