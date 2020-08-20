package com.example.moviemviimpl.state

data class MapsScreenViewState(


    val mapFields: MapsFields = MapsFields()

)


data class MapsFields(
    var lat: Double? = null,
    var lng: Double? = null
)