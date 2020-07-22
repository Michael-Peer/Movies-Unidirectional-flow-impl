package com.example.moviemviimpl

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moviemviimpl.state.MainScreenStateEvent
import com.example.moviemviimpl.utils.DataStateListener
import com.example.moviemviimpl.viewmodels.MainViewModel

/**
 *
 * The general process:
 *
 *Fire a state event from the fragment to the view model
 * update the main activity with current state(loading, dialog etc) through interface
 * DataState(result) returned from the view model to the fragment
 * update the ui
 *
 *
 **/


class MainFragment : Fragment() {
    private val TAG = "MainFragment"

    lateinit var mainViewModel: MainViewModel

    lateinit var dataStateListener: DataStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        triggerGetMoviesEvent()

        mainViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            Log.d(TAG, "onViewCreated: DataState $dataState ")

            /**
             *
             * Handle Error & Loading in the main activity
             *
             */
            dataStateListener.onDataStateChange(dataState)


            /**
             *
             * Handle Data
             *
             */
            dataState.data?.let { mainScreenViewStateEvent ->
                mainScreenViewStateEvent.getContentIfNotHandled()?.let { mainViewState ->
                    mainViewState.movies?.let { movies ->
                        //set movies data
                        mainViewModel.setMoviedData(movies)
                    }
                }


            }


        })

        mainViewModel.viewState.observe(viewLifecycleOwner, Observer {

            Log.d(TAG, "inside viewState observer $it")

            it.movies?.let {
                //set movies to recycler view
            }
        })
    }

    fun triggerGetMoviesEvent() {
        mainViewModel.setStateEvent(MainScreenStateEvent.GetAllMovies)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateListener = context as DataStateListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "onAttach: $e")
        }
    }

}