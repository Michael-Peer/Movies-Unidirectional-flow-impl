package com.example.moviemviimpl.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.repository.MainRepository
import com.example.moviemviimpl.state.MainScreenStateEvent
import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.utils.DataState

class MainViewModel : BaseViewModel() {

    private val _viewState: MutableLiveData<MainScreenViewState> = MutableLiveData()

    private val _stateEvent: MutableLiveData<MainScreenStateEvent> = MutableLiveData()

    val viewState: LiveData<MainScreenViewState>
        get() = _viewState

    val dataState: LiveData<DataState<MainScreenViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            handleStateEvent(stateEvent)
        }


    private fun handleStateEvent(stateEvent: MainScreenStateEvent): LiveData<DataState<MainScreenViewState>> {
        when (stateEvent) {
            is MainScreenStateEvent.GetAllMovies -> {
//                return AbsentLiveData.create()
               return MainRepository.getMovies()
            }
        }
    }

    fun setMoviedData(movies: List<Movie>) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movies = movies
        _viewState.value = updatedViewState
    }

    fun getCurrentViewStateOrNew(): MainScreenViewState {
        val viewState = viewState.value?.let {
            it
        } ?: MainScreenViewState()
        return viewState
    }

    /**
     *
     *
     * Here we set the event for what we want to happen
     * for example, if we want to get movies
     * the state event wil be: MainScreenStateEvent.getMovies()
     * basically it's the action we wanna to do
     *
     */
    fun setStateEvent(event: MainScreenStateEvent) {
        _stateEvent.value = event
    }
}