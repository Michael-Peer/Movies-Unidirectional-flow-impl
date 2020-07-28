package com.example.moviemviimpl.repository

import android.util.Log
import com.example.moviemviimpl.api.MoviesApi
import com.example.moviemviimpl.cache.MovieDao
import com.example.moviemviimpl.model.MovieImages
import com.example.moviemviimpl.state.DetailScreenViewState
import com.example.moviemviimpl.state.MoviesDetailsFields
import com.example.moviemviimpl.utils.ApiResponseHandler
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.StateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DetailRepositoryImpl
@Inject
constructor(
    private val moviesApi: MoviesApi,
    private val movieDao: MovieDao

) : DetailRepository {
    private val TAG = "DetailRepositoryImpl"

    override fun getMovieImage(
        stateEvent: StateEvent,
        movieId: Int
    ): Flow<DataState<DetailScreenViewState>> {
        Log.d(TAG, "getMovieImage: state")
        return flow {
            val apiResult = safeApiCall(Dispatchers.IO) {
                moviesApi.getMovieImages(
                    apiKey = Constants.API_KEY,
                    movieId = movieId
                )
            }
            emit(object : ApiResponseHandler<DetailScreenViewState, MovieImages>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: MovieImages): DataState<DetailScreenViewState> {

                    Log.d(TAG, "handleSuccess: $resultObj")
                    Log.d(TAG, "handleSuccess: ${resultObj.id}")
                    Log.d(TAG, "handleSuccess: ${resultObj.backdrops}")
                    Log.d(TAG, "handleSuccess: ${resultObj.posters}")

                    val viewState = DetailScreenViewState(
                        movieDetailFields = MoviesDetailsFields(
                            movieImages = resultObj
                        )
                    )
                    return DataState.data(
                        response = null,
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }

            }.getResult())
        }
    }


}