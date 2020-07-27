package com.example.moviemviimpl.repository

import android.util.Log
import com.example.moviemviimpl.api.MoviesApi
import com.example.moviemviimpl.cache.MovieDao
import com.example.moviemviimpl.cache.returnOrderedMovies
import com.example.moviemviimpl.model.Movie
import com.example.moviemviimpl.model.Movies
import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.state.MoviesFields
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.StateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
class MainRepositoryImpl
@Inject
constructor(
    private val moviesApi: MoviesApi,
    private val movieDao: MovieDao

) : MainRepository {
    private val TAG = ""
    override fun getMovies(
        stateEvent: StateEvent,
        order: String
    ): Flow<DataState<MainScreenViewState>> {

        return object : NetworkBoundResource<Movies, List<Movie>, MainScreenViewState>(
            dispatcher = Dispatchers.IO,
            stateEvent = stateEvent,
            apiCall = {
                moviesApi.getAllMovies(Constants.API_KEY)
            },
            cacheCall = {
//                movieDao.getAllMoviesFromDBOrderedByTitle()
                movieDao.returnOrderedMovies(order)
            }
        ) {
            override suspend fun updateCache(networkObject: Movies) {
                val moviesList = networkObject.movies?.toList()
                withContext(Dispatchers.IO) {
                    moviesList?.let { moviesList ->
                        for (movie in moviesList) {
                            try {
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting movie $movie")
                                    movieDao.insert(movie)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "updateLocalDb: error updating cache data on blog post with title: ${movie.title}. " +
                                            "${e.message}"
                                )
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }
                    }
                }

            }

            override fun handleCacheSuccess(resultObj: List<Movie>): DataState<MainScreenViewState> {
                val viewState = MainScreenViewState(
//                    movies = resultObj
                    moviesFields = MoviesFields(
                        order = order,
                        movies = resultObj
                    )
                )
                return DataState.data(
                    response = null,
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

        }.result
    }


    /**
     *
     * Movies - ResponseObject
     * MainScreenViewState - ViewStateType
     *
     */

//    fun getMovies(): LiveData<DataState<MainScreenViewState>> {
//        return object : NetworkBoundResource<Movies, MainScreenViewState>() {
//
//            override fun handleApiSuccessResponse(response: ApiSuccessResponse<Movies>) {
//                /**
//                 * referring to the MediatorLiveData - we can refer this because it's protected value
//                 *
//                 * */
//                result.value = DataState.data(
//                    message = null,
//                    data = MainScreenViewState(
//                        movies = response.body.movies
//                    )
//                )
//            }
//
//            override fun createCall(): LiveData<GenericApiResponse<Movies>> {
////                return RetrofitService.apiService.getAllMovies(Constants.API_KEY)
//                return moviesApi.getAllMovies(Constants.API_KEY)
//            }
//
//        }.asLiveData()
//    }


}