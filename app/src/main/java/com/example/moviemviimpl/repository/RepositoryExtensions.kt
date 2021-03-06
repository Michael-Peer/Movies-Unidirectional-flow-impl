package com.example.moviemviimpl.repository

import android.util.Log
import com.bumptech.glide.load.HttpException
import com.example.moviemviimpl.utils.*
import com.example.moviemviimpl.utils.Constants.CACHE_TIMEOUT
import com.example.moviemviimpl.utils.Constants.NETWORK_TIMEOUT
import com.example.moviemviimpl.utils.ErrorHandling.Companion.CACHE_ERROR_TIMEOUT
import com.example.moviemviimpl.utils.ErrorHandling.Companion.NETWORK_ERROR_TIMEOUT
import com.example.moviemviimpl.utils.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException

private val TAG: String = "SafeCall"

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    Log.d(TAG, "safeApiCall: ")
    return withContext(dispatcher) {
        try {
            Log.d(TAG, "safeApiCall: try")
//             throws TimeoutCancellationException
//            withTimeout(NETWORK_TIMEOUT){
                ApiResult.Success(apiCall.invoke())
//            }
        } catch (throwable: Throwable) {
            Log.d(TAG, "safeApiCall: cactch")
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = convertErrorBody(throwable)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    ApiResult.GenericError(
                        null,
                        UNKNOWN_ERROR
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT){
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.GenericError(UNKNOWN_ERROR)
                }
            }
        }
    }
}


fun <ViewState> buildError(
    message: String,
    uiComponentType: UIComponentType,
    stateEvent: StateEvent?
): DataState<ViewState> {
    return DataState.error(
        response = Response(
            message = "${stateEvent?.errorInfo()}\n\nReason: ${message}",
            uiComponentType = uiComponentType,
            messageType = MessageType.Error
        ),
        stateEvent = stateEvent
    )

}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
//        throwable.response()?.errorBody()?.string()
        throwable.message
    } catch (exception: Exception) {
        UNKNOWN_ERROR
    }
}



