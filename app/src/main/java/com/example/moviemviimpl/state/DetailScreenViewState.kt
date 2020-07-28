package com.example.moviemviimpl.state

import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.model.MovieImages

data class DetailScreenViewState(


    val movieDetailFields: MoviesDetailsFields = MoviesDetailsFields()

)


data class MoviesDetailsFields(
    var movie: Movie? = null,
    var movieImages: MovieImages? = null
)