package com.example.moviemviimpl.state

import com.example.moviemviimpl.utils.StateEvent

sealed class DetailScreenStateEvent : StateEvent {

    object GetMovieImages : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movies images"
        }

        override fun toString(): String {
            return "GetMovieImages"
        }
    }

    object GetMovieTrailer : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movie trailer"
        }

        override fun toString(): String {
            return "GetMovieTrailer"
        }
    }
}