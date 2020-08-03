package com.example.moviemviimpl.api

import androidx.lifecycle.LiveData
import com.example.moviemviimpl.model.MovieImages
import com.example.moviemviimpl.model.Movies
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


//interface MoviesApi {
//    @GET(Constants.POPULAR_MOVIES_URL)
//    fun getAllMovies(
//        @Query("api_key") apiKey: String?
//    ): LiveData<GenericApiResponse<Movies>>
//}

interface MoviesApi {
    @GET(Constants.POPULAR_MOVIES_URL)
    suspend fun getAllMovies(
        @Query("api_key") apiKey: String
    ): Movies

    @GET("3/movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieImages
}