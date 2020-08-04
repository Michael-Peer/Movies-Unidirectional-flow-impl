package com.example.moviemviimpl.repository

import com.example.moviemviimpl.state.DetailScreenViewState
import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.StateEvent
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getMovieImage(stateEvent: StateEvent, movieId: Int): Flow<DataState<DetailScreenViewState>>

    fun getMovieTrailer(stateEvent: StateEvent, movieId: Int): Flow<DataState<DetailScreenViewState>>

}

