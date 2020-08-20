package com.example.moviemviimpl.viewmodels

import android.util.Log
import com.example.moviemviimpl.state.MapsScreenViewState
import com.example.moviemviimpl.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class MapsViewModel
@Inject
constructor(
) : BaseViewModel<MapsScreenViewState>() {

    private  val TAG = "MapsViewModel"

    override fun handleNewData(data: MapsScreenViewState) {
        Log.d(TAG, "handleNewData: $data")
        data.mapFields.let { mapFields ->

//            mapFields.lat?.let { movieImages ->
//                setImagesData(movieImages)
//            }

        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {

            val job: Flow<DataState<MapsScreenViewState>> = when (stateEvent) {

//                is MapsScreenStateEvent.GetUserLocation -> {
//
//
//                }


                else -> {
                    flow {
                        emit(
                            DataState.error<MapsScreenViewState>(
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

    override fun initNewViewState(): MapsScreenViewState {
        return MapsScreenViewState()
    }


}