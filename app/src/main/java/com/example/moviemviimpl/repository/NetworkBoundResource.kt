package com.example.moviemviimpl.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.moviemviimpl.utils.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * ResponseObject - response from retrofit
 *
 * ViewStateType - The type of the viewState we return from the functions inside the repository
 *
 **/

abstract class NetworkBoundResource<ResponseObject, ViewStateType> {

    protected val result = MediatorLiveData<DataState<ViewStateType>>()

    init {
        //showing progress bar
        result.value = DataState.loading(true)


        GlobalScope.launch(IO) {
//            delay(TESTING_NETWORK_DELAY)

            withContext(Main) {
                /**
                 *
                 * createCall() - abstract function.
                 * we implement this inside the repository - RetrofitService.getMovies, RetrofitService.getCredits etc
                 * returns LiveData<genericApiResponse<ResponseObject>>
                 *
                 * **/
                val apiResponse = createCall()
                /**
                 *
                 * We ass the response from to the api to Mediator live data source
                 * the response is GenericApiResponse<ResponseObject>
                 * then we handle all generic response states
                 *
                 */
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)

                    handleNetworkCall(response)
                }
            }
        }
    }

    /**
     *
     * handleApiSuccessResponse - abstract function we implement this inside the repository
     * handle what to do when we get successful response from the api
     *
     * onReturnError - we set the result to the error message
     *
     */

    fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

        when (response) {

            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                println("DEBUG: NetworkBoundResource: ${response.errorMessage}")
                onReturnError(response.errorMessage)
            }
            is ApiEmptyResponse -> {
                println("DEBUG: NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onReturnError("HTTP 204. Returned NOTHING.")
            }
        }
    }

    fun onReturnError(message: String) {
        result.value = DataState.error(message)
    }

    abstract fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    /**
     *
     * Convert MediatorLiveData to LiveData
     *
     * **/

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
}