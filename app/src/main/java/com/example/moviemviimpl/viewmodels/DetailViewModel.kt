package com.example.moviemviimpl.viewmodels

import android.util.Log
import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.model.MovieImages
import com.example.moviemviimpl.repository.DetailRepository
import com.example.moviemviimpl.repository.DetailRepositoryImpl
import com.example.moviemviimpl.state.DetailScreenStateEvent
import com.example.moviemviimpl.state.DetailScreenViewState
import com.example.moviemviimpl.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class DetailViewModel
@Inject
constructor(
    private val moviesDetailRepository: DetailRepository
) : BaseViewModel<DetailScreenViewState>() {
    private val TAG = "DetailViewModel"


    fun setMovieData(movie: Movie) {
        Log.d(TAG, "SetFlow: inside  setMovieData $movie")
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movie = movie
        Log.d(TAG, "SetFlow: inside  updatedViewState ${updatedViewState.movieDetailFields}")
        setViewState(updatedViewState)
    }

    private fun getMovie(): Movie {
        getCurrentViewStateOrNew().let {
            return it.movieDetailFields.movie?.let { movie ->
                return movie
            } ?: throw IllegalArgumentException("Unable get movie")
        }
    }


    private fun setImagesData(movieImages: MovieImages) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieImages = movieImages
        setViewState(updatedViewState)
    }

    override fun handleNewData(data: DetailScreenViewState) {
        Log.d(TAG, "handleNewData: $data")
        data.movieDetailFields.let { movieDetailFields ->
            movieDetailFields.movieImages?.let { movieImages ->
                setImagesData(movieImages)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<DetailScreenViewState>> = when (stateEvent) {
            is DetailScreenStateEvent.getMovieImages -> {
                flow {
                    val movie: Movie? = getMovie()
                    movie?.let {
                        moviesDetailRepository.getMovieImage(
                            stateEvent = stateEvent,
                            movieId = it.id!!
                        )
                    }

                }
            }
            else -> {
                flow {
                    emit(
                        DataState.error<DetailScreenViewState>(
                            response = Response(
                                message = ErrorHandling.INVALID_STATE_EVENT,
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


    override fun initNewViewState(): DetailScreenViewState {
        return DetailScreenViewState()
    }

}