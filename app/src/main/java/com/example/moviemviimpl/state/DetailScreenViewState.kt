package com.example.moviemviimpl.state

import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.model.MovieDetail
import com.example.moviemviimpl.model.MovieImages
import com.example.moviemviimpl.model.Trailers

data class DetailScreenViewState(


    val movieDetailFields: MoviesDetailsFields = MoviesDetailsFields()

)


data class MoviesDetailsFields(
    var movieId: Int? = null,
    var movieDetails: MovieDetail? = null,
    var movieImages: MovieImages? = null,
    var movieTrailers: Trailers? = null
)