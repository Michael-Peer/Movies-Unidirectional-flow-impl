package com.example.moviemviimpl.state

import com.example.moviemviimpl.model.Movie

data class MainScreenViewState(
    var movies: List<Movie>? = null
)