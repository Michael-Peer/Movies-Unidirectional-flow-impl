package com.example.moviemviimpl.state

import com.example.moviemviimpl.utils.StateEvent

sealed class DetailScreenStateEvent : StateEvent {

    object getMovieImages : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movies"
        }

        override fun toString(): String {
            return "getMoviePhotos"
        }
    }
    }