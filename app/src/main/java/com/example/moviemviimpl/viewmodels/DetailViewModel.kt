package com.example.moviemviimpl.viewmodels

import android.util.Log
import com.example.moviemviimpl.model.*
import com.example.moviemviimpl.repository.DetailRepository
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

    init {
        Log.d(TAG, "$moviesDetailRepository ")
    }


    fun setMovieIdData(movieID: Int) {
        Log.d(TAG, "SetFlow: inside  setMovieData $movieID")
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieId = movieID
        Log.d(TAG, "SetFlow: inside  updatedViewState ${updatedViewState.movieDetailFields}")
        setViewState(updatedViewState)
    }

    private fun getMovieID(): Int {
        getCurrentViewStateOrNew().let {
            return it.movieDetailFields.movieId?.let { movieID ->
                return movieID
            } ?: throw IllegalArgumentException("Unable get movie")
        }
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {

            val job: Flow<DataState<DetailScreenViewState>> = when (stateEvent) {

                is DetailScreenStateEvent.GetMovieImages -> {
                    moviesDetailRepository.getMovieImage(
                        stateEvent = stateEvent,
                        movieId = getMovieID()
                    )
                }

                is DetailScreenStateEvent.GetMovieTrailer -> {
                    moviesDetailRepository.getMovieTrailer(
                        stateEvent = stateEvent,
                        movieId = getMovieID()
                    )
                }

                is DetailScreenStateEvent.GetMovieDetail -> {
                    moviesDetailRepository.getMovieDetail(
                        stateEvent = stateEvent,
//                        movieId = getMovie().id!!
                        movieId = getMovieID()
                    )
                }

                is DetailScreenStateEvent.GetMovieCredits -> {
                    moviesDetailRepository.getMovieCredits(
                        stateEvent = stateEvent,
                        movieId = getMovieID()
                    )
                }

                is DetailScreenStateEvent.GetSimilarMovies -> {
                    moviesDetailRepository.getSimilarMovies(
                        stateEvent = stateEvent,
                        movieId = getMovieID()
                    )
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
    }

    override fun handleNewData(data: DetailScreenViewState) {
        Log.d(TAG, "handleNewData: $data")
        data.movieDetailFields.let { movieDetailFields ->

            movieDetailFields.movieImages?.let { movieImages ->
                setImagesData(movieImages)
            }

            movieDetailFields.movieTrailers?.let { movieTrailers ->
                setMovieTrailers(movieTrailers)
            }

            movieDetailFields.movieDetails?.let { movieDetails ->
                setMovieDetails(movieDetails)
            }

            movieDetailFields.movieCredits?.let { movieCredits ->
                setMovieCredits(movieCredits)
            }

            movieDetailFields.similarMovies?.let { similarMovies ->
                setSimilarMovies(similarMovies)
            }
        }
    }

    private fun setMovieDetails(movieDetails: MovieDetail) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieDetails = movieDetails
        setViewState(updatedViewState)
    }

    private fun setImagesData(movieImages: MovieImages) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieImages = movieImages
        setViewState(updatedViewState)
    }

    private fun setMovieTrailers(movieTrailers: Trailers) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieTrailers = movieTrailers
        setViewState(updatedViewState)
    }

    private fun setMovieCredits(movieCredits: Credits) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.movieCredits = movieCredits
        setViewState(updatedViewState)
    }

    private fun setSimilarMovies(similarMovies: Movies) {
        val updatedViewState = getCurrentViewStateOrNew()
        updatedViewState.movieDetailFields.similarMovies = similarMovies
        setViewState(updatedViewState)
    }


    override fun initNewViewState(): DetailScreenViewState {
        return DetailScreenViewState()
    }

    fun extractTrailer(): String? {
        val trailers = getCurrentViewStateOrNew().movieDetailFields.movieTrailers?.trailers
        trailers?.let { trailersList ->
            for (trailer in trailersList) {
                if (trailer.site == "YouTube") {
                    Log.d(TAG, "extractTrailer papapa: YouTube")
                    //Returning the first youtube trailer
                    return trailer.key
                }
            }
        }
        return null
    }

    fun getGeners(genres: List<Genre>?): String {
        var stringGeners = "There is no available genres for this movie"

        genres?.let { genresList ->
            stringGeners = ""

//            for (genre in genresList) {
//                stringGeners += ", ${genre.name}"
//            }

            genresList.forEachIndexed { index, genre ->
//                if (index == 0) {
//                    stringGeners += " ${genre.name}"
//                } else {
//                    stringGeners += ", ${genre.name}"
//
//                }

                stringGeners += if (index == 0) {
                    "${genre.name}"
                } else {
                    ", ${genre.name}"

                }
            }
        }

//        return stringGeners ?: "There is no available genres to this movie"
        return stringGeners


    }

    fun getCountries(productionCountries: List<ProductionCountry>?): String {
        var stringCountries = "There is no available genres for this movie"


        productionCountries?.let {
            stringCountries = ""

            productionCountries.forEachIndexed { index, productionCountry ->
                stringCountries += if (index == 0) {
                    "${productionCountry.name}"
                } else {
                    ", ${productionCountry.name}"
                }
            }
        }
        return stringCountries
    }




}