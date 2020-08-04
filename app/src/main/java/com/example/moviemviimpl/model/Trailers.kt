package com.example.moviemviimpl.model


import com.google.gson.annotations.SerializedName

data class Trailers(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("results")
    val trailers : List<Trailer>?
)