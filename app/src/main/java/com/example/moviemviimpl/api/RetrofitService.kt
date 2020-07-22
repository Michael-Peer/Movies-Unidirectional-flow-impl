package com.example.moviemviimpl.api

import com.example.moviemviimpl.utils.Constants.BASE_URL
import com.example.moviemviimpl.utils.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {


    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }


    val apiService: MoviesApi by lazy {
        retrofitBuilder
            .build()
            .create(MoviesApi::class.java)
    }
}