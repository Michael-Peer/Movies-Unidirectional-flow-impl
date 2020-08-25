package com.example.moviemviimpl.api

import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DummyRetrofit {

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
    }


    val apiService: DummyApi by lazy {
        retrofitBuilder
            .build()
            .create(DummyApi::class.java)
    }
}