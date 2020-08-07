package com.example.moviemviimpl.model


import com.google.gson.annotations.SerializedName

data class Credits(
    @SerializedName("cast")
    val cast: List<Cast>?,
    @SerializedName("crew")
    val crew: List<Crew>?,
    @SerializedName("id")
    val id: Int?
)