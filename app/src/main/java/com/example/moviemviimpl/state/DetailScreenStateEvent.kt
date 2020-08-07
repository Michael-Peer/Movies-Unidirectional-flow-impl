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

    object GetMovieDetail : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movie details"
        }

        override fun toString(): String {
            return "GetMovieDetail"
        }
    }

    object GetMovieCredits : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get movie credits"
        }

        override fun toString(): String {
            return "GetMovieCredits"
        }
    }

    object GetSimilarMovies : DetailScreenStateEvent() {

        override fun errorInfo(): String {
            return "Error While Trying to get similar movies"
        }

        override fun toString(): String {
            return "GetSimilarMovies"
        }
    }
}