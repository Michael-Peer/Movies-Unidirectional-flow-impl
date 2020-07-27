package com.example.moviemviimpl.state

import com.example.moviemviimpl.utils.StateEvent

sealed class MainScreenStateEvent : StateEvent {

    object GetAllMovies : MainScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movies"
        }

        override fun toString(): String {
            return "GetAllMovies"
        }
    }

    object OrderByMovies : MainScreenStateEvent() {
        override fun errorInfo(): String {
            return "Enable to filter movies"
        }

        override fun toString(): String {
            return "OrderByMovies"
        }
    }

}