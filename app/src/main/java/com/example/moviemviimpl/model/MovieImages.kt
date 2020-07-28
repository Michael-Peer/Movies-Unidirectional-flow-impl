package com.example.moviemviimpl.model


import com.google.gson.annotations.SerializedName

data class MovieImages
    (
    @SerializedName("backdrops")
    val backdrops: List<Backdrop>?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("posters")
    val posters: List<Poster>?
)