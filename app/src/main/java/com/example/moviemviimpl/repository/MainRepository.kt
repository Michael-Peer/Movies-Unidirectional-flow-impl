package com.example.moviemviimpl.repository

import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.StateEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository  {
    fun getMovies(stateEvent: StateEvent): Flow<DataState<MainScreenViewState>>
}