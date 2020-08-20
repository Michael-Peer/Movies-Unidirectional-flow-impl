package com.example.moviemviimpl.state

import com.example.moviemviimpl.utils.StateEvent

sealed class MapsScreenStateEvent : StateEvent {

    object GetUserLocation : MapsScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get user location"
        }

        override fun toString(): String {
            return "GetUserLocation"
        }
    }

}