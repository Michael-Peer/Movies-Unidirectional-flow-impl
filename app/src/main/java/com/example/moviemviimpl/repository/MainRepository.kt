package com.example.moviemviimpl.repository

import androidx.lifecycle.LiveData
import com.example.moviemviimpl.api.RetrofitService
import com.example.moviemviimpl.model.Movies
import com.example.moviemviimpl.state.MainScreenViewState
import com.example.moviemviimpl.utils.ApiSuccessResponse
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.GenericApiResponse

object MainRepository {

    /**
     *
     * Movies - ResponseObject
     * MainScreenViewState - ViewStateType
     *
     */


    fun getMovies(): LiveData<DataState<MainScreenViewState>> {
        return object : NetworkBoundResource<Movies, MainScreenViewState>() {

            override fun handleApiSuccessResponse(response: ApiSuccessResponse<Movies>) {
                /**
                 * referring to the MediatorLiveData - we can refer this because it's protected value
                 *
                 * */
                result.value = DataState.data(
                    message = null,
                    data = MainScreenViewState(
                        movies = response.body.movies
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<Movies>> {
                return RetrofitService.apiService.getAllMovies(Constants.API_KEY)
            }

        }.asLiveData()
    }


}