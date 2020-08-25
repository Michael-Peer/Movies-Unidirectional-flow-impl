package com.example.moviemviimpl.api

import com.example.moviemviimpl.dummyResponseDirection.DirectionResponse
import com.example.moviemviimpl.model.MovieImages
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyApi  {
    @GET("maps/api/directions/{output}")
    suspend fun getRoute(
        @Path("output") output: String,
        @Query("key") apiKey: String,
        @Query("origin") origin: String,
        @Query("destination") dest: String,
        @Query("sensor") sensor: String,
        @Query("mode") mode: String
    ): DirectionResponse
}