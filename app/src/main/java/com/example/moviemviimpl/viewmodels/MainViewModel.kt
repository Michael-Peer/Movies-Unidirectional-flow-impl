package com.example.moviemviimpl.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.repository.MainRepository
import com.example.moviemviimpl.state.MainScreenStateEvent
import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.utils.*
import com.example.moviemviimpl.utils.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class MainViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferences: SharedPreferences,
    private val sharedPreferencesEditor: SharedPreferences.Editor
) : BaseViewModel() {
    private val TAG = "MainViewModel"

    private val _viewState: MutableLiveData<MainScreenViewState> = MutableLiveData()

    private val _stateEvent: MutableLiveData<MainScreenStateEvent> = MutableLiveData()


    val viewState: LiveData<MainScreenViewState>
        get() = _viewState

//    val dataState: LiveData<DataState<MainScreenViewState>> = Transformations
//        .switchMap(_stateEvent) { stateEvent ->
//            handleStateEvent(stateEvent)
//        }


//    init {
//        setMoviesFilter(sharedPreferences.getString(
//            PreferenceKeys.BLOG_FILTER,
//
//        ))
//    }

    private val dataChannelManager: DataChannelManager<MainScreenViewState> =
        object : DataChannelManager<MainScreenViewState>() {
            override fun handleNewData(data: MainScreenViewState) {
//                data.movies.let { movies ->
                data.moviesFields.movies.let { movies ->
                    movies.let { moviesList ->
                        setMoviedData(moviesList!!)
                    }
                }
            }
        }

    val numActiveJobs: LiveData<Int> = dataChannelManager.numActiveJobs

    val stateMessage: LiveData<StateMessage?>
        get() = dataChannelManager.messageStack.stateMessage

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<MainScreenViewState>>
    ) {
        dataChannelManager.launchJob(stateEvent, jobFunction)
    }


//    private fun handleStateEvent(stateEvent: MainScreenStateEvent): LiveData<DataState<MainScreenViewState>> {
//        when (stateEvent) {
//            is MainScreenStateEvent.GetAllMovies -> {
////                return AbsentLiveData.create()
//                return mainRepository.getMovies()
//            }
//        }
//    }

    fun setMoviedData(movies: List<Movie>) {
        val updatedViewState = getCurrentViewStateOrNew()
//        updatedViewState.movies = movies
        updatedViewState.moviesFields.movies = movies
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
     * here we set the view state
     *
     * **/
    fun setViewState(viewState: MainScreenViewState) {
        _viewState.value = viewState
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
//    fun setStateEvent(event: MainScreenStateEvent) {
//        _stateEvent.value = event
//    }

    fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<MainScreenViewState>> = when (stateEvent) {

            is MainScreenStateEvent.GetAllMovies -> {
                mainRepository.getMovies(stateEvent = stateEvent)
            }
            else -> {
                flow {
                    emit(
                        DataState.error<MainScreenViewState>(
                            response = Response(
                                message = INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None,
                                messageType = MessageType.Error
                            ),
                            stateEvent = stateEvent
                        )
                    )
                }
            }

        }
        launchJob(stateEvent, job)
    }

    fun setupChannel() = dataChannelManager.setupChannel()


    fun clearStateMessage(index: Int = 0) {
        dataChannelManager.clearStateMessage(index)
    }

    fun areAnyJobsActive(): Boolean {
        return dataChannelManager.numActiveJobs.value?.let {
            it > 0
        } ?: false
    }

    fun cancelActiveJobs() {
        if (areAnyJobsActive()) {
            Log.d(TAG, "cancel active jobs: ${dataChannelManager.numActiveJobs.value ?: 0}")
            dataChannelManager.cancelJobs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun getOrder(): String {
        return getCurrentViewStateOrNew().moviesFields.order ?: Constants.ORDER_BY_TITLE
    }

    /**
     *
     * Save the filter that selected by the user to shared preferences
     * **/
    fun saveFilterOptions(filter: String) {
        sharedPreferencesEditor.putString(PreferenceKeys.BLOG_FILTER, filter)
        sharedPreferencesEditor.apply()
    }

    fun setMoviesFilter(filter: String?) {
        filter?.let {
            val updatedViewState = getCurrentViewStateOrNew()
            updatedViewState.moviesFields.order = filter
            setViewState(updatedViewState)
        }
    }
}