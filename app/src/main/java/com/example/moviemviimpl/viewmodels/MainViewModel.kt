package com.example.moviemviimpl.viewmodels

import android.content.SharedPreferences
import android.util.Log
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
) : BaseViewModel<MainScreenViewState>() {

    private val TAG = "MainViewModel"


    init {
        //(key, defValue)
        setMoviesFilter(
            sharedPreferences.getString(
                PreferenceKeys.BLOG_FILTER, Constants.ORDER_BY_TITLE
            )
        )
    }


    //IN BASE
//    private val _viewState: MutableLiveData<MainScreenViewState> = MutableLiveData()

//    private val _stateEvent: MutableLiveData<MainScreenStateEvent> = MutableLiveData()


//    val viewState: LiveData<MainScreenViewState>
//        get() = _viewState


//    val dataState: LiveData<DataState<MainScreenViewState>> = Transformations
//        .switchMap(_stateEvent) { stateEvent ->
//            handleStateEvent(stateEvent)
//        }


//    val numActiveJobs: LiveData<Int> = dataChannelManager.numActiveJobs
//
//    val stateMessage: LiveData<StateMessage?>
//        get() = dataChannelManager.messageStack.stateMessage
//
//    fun launchJob(
//        stateEvent: StateEvent,
//        jobFunction: Flow<DataState<MainScreenViewState>>
//    ) {
//        dataChannelManager.launchJob(stateEvent, jobFunction)
//    }


//    private fun handleStateEvent(stateEvent: MainScreenStateEvent): LiveData<DataState<MainScreenViewState>> {
//        when (stateEvent) {
//            is MainScreenStateEvent.GetAllMovies -> {
////                return AbsentLiveData.create()
//                return mainRepository.getMovies()
//            }
//        }
//    }

    private fun setMovieData(movies: List<Movie>) {
        val updatedViewState = getCurrentViewStateOrNew()
//        updatedViewState.movies = movies
        updatedViewState.moviesFields.movies = movies
        setViewState(updatedViewState)
    }

//    private fun getCurrentViewStateOrNew(): MainScreenViewState {
//        val viewState = viewState.value?.let {
//            it
//        } ?: MainScreenViewState()
//        return viewState
//    }

    /**
     *
     * here we set the view state
     *
     * **/
//    fun setViewState(viewState: MainScreenViewState) {
//        _viewState.value = viewState
//    }

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


//    fun setupChannel() = dataChannelManager.setupChannel()
//
//
//    fun clearStateMessage(index: Int = 0) {
//        dataChannelManager.clearStateMessage(index)
//    }
//
//    fun areAnyJobsActive(): Boolean {
//        return dataChannelManager.numActiveJobs.value?.let {
//            it > 0
//        } ?: false
//    }
//
//    fun cancelActiveJobs() {
//        if (areAnyJobsActive()) {
//            Log.d(TAG, "cancel active jobs: ${dataChannelManager.numActiveJobs.value ?: 0}")
//            dataChannelManager.cancelJobs()
//        }
//    }

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
     *
     * key storing:
     * unique key to get access to the store
     * PreferenceKeys.BLOG_FILTER = com.example.moviemviimpl.MOVIE_FILTER
     *
     * filter  = Constants.ORDER_BY_YEAR/Constants.ORDER_BY_TITLE
     *
     * **/
    fun saveFilterOptions(filter: String) {
        Log.d(TAG, "saveFilterOptions: $filter")

        sharedPreferencesEditor.putString(PreferenceKeys.BLOG_FILTER, filter)
        sharedPreferencesEditor.apply()
    }


    /**
     *
     * filter  = Constants.ORDER_BY_YEAR/Constants.ORDER_BY_TITLE
     *
     * Here we set the order field in movies field inside the view state to the filter we get from the user
     * then we update the view state, to the most updayted view state
     *
     * **/
    fun setMoviesFilter(filter: String?) {
        Log.d(TAG, "setMoviesFilter: $filter")
        filter?.let {
            val updatedViewState = getCurrentViewStateOrNew()
            updatedViewState.moviesFields.order = filter
            setViewState(updatedViewState)
        }
    }

    fun loadOrderedPage() {
        setStateEvent(MainScreenStateEvent.GetAllMovies)
    }

    override fun handleNewData(data: MainScreenViewState) {
        data.moviesFields.let { moviesFields ->
            moviesFields.movies?.let { movies ->
                setMovieData(movies)
            }
        }
    }

//    private fun handleData(data: MainScreenViewState) {
//        data.moviesFields.let { moviesFields ->
//            moviesFields.movies?.let { movies ->
//                setMoviedData(movies)
//            }
//        }
//    }

    //    private val dataChannelManager: DataChannelManager<MainScreenViewState> =
//        object : DataChannelManager<MainScreenViewState>() {
//            override fun handleNewData(data: MainScreenViewState) {
//                handleData(data)
////                data.movies.let { movies ->
////                data.moviesFields.movies.let { movies ->
////                    movies.let { moviesList ->
////                        setMoviedData(moviesList!!)
////                    }
////                }
//            }
//        }

    override fun initNewViewState(): MainScreenViewState {
        return MainScreenViewState()
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<MainScreenViewState>> = when (stateEvent) {

                is MainScreenStateEvent.GetAllMovies, MainScreenStateEvent.OrderByMovies -> {
                    mainRepository.getMovies(stateEvent = stateEvent, order = getOrder())
                }

//            is  -> {
//                mainRepository.getMovies(stateEvent, getOrder())
//            }
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

    }

//        fun setStateEvent(stateEvent: StateEvent) {
//        val job: Flow<DataState<MainScreenViewState>> = when (stateEvent) {
//
//            is MainScreenStateEvent.GetAllMovies, MainScreenStateEvent.OrderByMovies -> {
//                mainRepository.getMovies(stateEvent = stateEvent, order = getOrder())
//            }
//
////            is  -> {
////                mainRepository.getMovies(stateEvent, getOrder())
////            }
//            else -> {
//                flow {
//                    emit(
//                        DataState.error<MainScreenViewState>(
//                            response = Response(
//                                message = INVALID_STATE_EVENT,
//                                uiComponentType = UIComponentType.None,
//                                messageType = MessageType.Error
//                            ),
//                            stateEvent = stateEvent
//                        )
//                    )
//                }
//            }
//
//        }
//        launchJob(stateEvent, job)
//    }


}