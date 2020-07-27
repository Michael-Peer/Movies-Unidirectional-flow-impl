package com.example.moviemviimpl.repository

import com.example.moviemviimpl.utils.*
import com.example.moviemviimpl.utils.ErrorHandling.Companion.NETWORK_ERROR
import com.example.moviemviimpl.utils.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


@FlowPreview
abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
) {

    private val TAG: String = "AppDebug"

    val result: Flow<DataState<ViewState>> = flow {

        // ****** STEP 1: VIEW CACHE ******
        /**
         *
         *
         * In this step, we emit(return) the cache to UI
         * After this we make api call, insert data to cache, and return cache to ui again
         *
         **/
        emit(returnCache(markJobComplete = false))

        // ****** STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
        /**
         *
         * In this step we're making the network call
         * we hold the result in apiResult  val
         * The type of apiResult val is ApiResult that hold NetworkObject
         *
         */
        val apiResult = safeApiCall(dispatcher) { apiCall.invoke() }

        when (apiResult) {
            is ApiResult.GenericError -> {
                emit(
                    buildError<ViewState>(
                        apiResult.errorMessage?.let { it } ?: UNKNOWN_ERROR,
                        UIComponentType.Dialog,
                        stateEvent
                    )
                )
            }

            is ApiResult.NetworkError -> {
                emit(
                    buildError<ViewState>(
                        NETWORK_ERROR,
                        UIComponentType.Dialog,
                        stateEvent
                    )
                )
            }

            is ApiResult.Success -> {
                if (apiResult.value == null) {
                    emit(
                        buildError<ViewState>(
                            UNKNOWN_ERROR,
                            UIComponentType.Dialog,
                            stateEvent
                        )
                    )
                } else {
                    updateCache(apiResult.value as NetworkObj)
                }
            }
        }

        // ****** STEP 3: VIEW CACHE and MARK JOB COMPLETED ******
        emit(returnCache(markJobComplete = true))
    }

    private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState> {

        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

        var jobCompleteMarker: StateEvent? = null
        if (markJobComplete) {
            jobCompleteMarker = stateEvent
        }

        return object : CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ) {
            override suspend fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return handleCacheSuccess(resultObj)
            }
        }.getResult()

    }

    abstract suspend fun updateCache(networkObject: NetworkObj)

    abstract fun handleCacheSuccess(resultObj: CacheObj): DataState<ViewState> // make sure to return null for stateEvent


}


///**
// *
// * ResponseObject - response from retrofit
// *
// * ViewStateType - The type of the viewState we return from the functions inside the repository
// *
// **/
//
//abstract class NetworkBoundResource<ResponseObject, ViewStateType> {
//
//    protected val result = MediatorLiveData<DataState<ViewStateType>>()
//
//    init {
//        //showing progress bar
//        result.value = DataState.loading(true)
//
//
//        GlobalScope.launch(IO) {
////            delay(TESTING_NETWORK_DELAY)
//
//            withContext(Main) {
//                /**
//                 *
//                 * createCall() - abstract function.
//                 * we implement this inside the repository - RetrofitService.getMovies, RetrofitService.getCredits etc
//                 * returns LiveData<genericApiResponse<ResponseObject>>
//                 *
//                 * **/
//                val apiResponse = createCall()
//                /**
//                 *
//                 * We ass the response from to the api to Mediator live data source
//                 * the response is GenericApiResponse<ResponseObject>
//                 * then we handle all generic response states
//                 *
//                 */
//                result.addSource(apiResponse) { response ->
//                    result.removeSource(apiResponse)
//
//                    handleNetworkCall(response)
//                }
//            }
//        }
//    }
//
//    /**
//     *
//     * handleApiSuccessResponse - abstract function we implement this inside the repository
//     * handle what to do when we get successful response from the api
//     *
//     * onReturnError - we set the result to the error message
//     *
//     */
//
//    fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {
//
//        when (response) {
//
//            is ApiSuccessResponse -> {
//                handleApiSuccessResponse(response)
//            }
//
//            is ApiErrorResponse -> {
//                println("DEBUG: NetworkBoundResource: ${response.errorMessage}")
//                onReturnError(response.errorMessage)
//            }
//            is ApiEmptyResponse -> {
//                println("DEBUG: NetworkBoundResource: Request returned NOTHING (HTTP 204)")
//                onReturnError("HTTP 204. Returned NOTHING.")
//            }
//        }
//    }
//
//    fun onReturnError(message: String) {
//        result.value = DataState.error(message)
//    }
//
//    abstract fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)
//
//    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
//
//    /**
//     *
//     * Convert MediatorLiveData to LiveData
//     *
//     * **/
//
//    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
//}