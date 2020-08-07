package com.example.moviemviimpl.state

import com.example.moviemviimpl.model.*

data class DetailScreenViewState(


    val movieDetailFields: MoviesDetailsFields = MoviesDetailsFields()

)


data class MoviesDetailsFields(
    var movieId: Int? = null,
    var movieDetails: MovieDetail? = null,
    var movieImages: MovieImages? = null,
    var movieTrailers: Trailers? = null,
    var movieCredits: Credits? = null,
    var similarMovies: Movies? = null
)