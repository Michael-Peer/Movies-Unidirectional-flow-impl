package com.example.moviemviimpl.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviemviimpl.R
import com.example.moviemviimpl.state.DetailScreenStateEvent
import com.example.moviemviimpl.utils.StateMessageCallback
import com.example.moviemviimpl.utils.UICommunicationListener
import com.example.moviemviimpl.viewmodels.DetailViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class DetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment() {
    private val TAG = "DetailFragment"

    lateinit var uiCommunicationListener: UICommunicationListener


    private val args: DetailFragmentArgs by navArgs()

    private val detailViewModel: DetailViewModel by viewModels {
        viewModelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailViewModel.setMovieData(args.Movie)
        Log.d(TAG, "SetFlow: ${args.Movie.title}")


        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_de)
        val navHostFragment = NavHostFragment.findNavController(this)

        val collapsingToolbarLayout =
            view.findViewById<CollapsingToolbarLayout>(R.id.coolaps_tool_bar)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        textView11.text = args.Movie.title
        collapsingToolbarLayout.title = args.Movie.title
        loadDetailImage()



        subscribeMoviesObserver()

        detailViewModel.setStateEvent(DetailScreenStateEvent.getMovieImages)
    }

    private fun loadDetailImage() {
        val currentUrl = "https://image.tmdb.org/t/p/w400" + args.Movie.posterPath
        Glide.with(this)
            .load(currentUrl)
            .apply(RequestOptions().override(400, 600))
            .placeholder(R.drawable.ic_launcher_background)
            .into(detailImage)
    }

    private fun subscribeMoviesObserver() {


        detailViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                //set fields
                Log.d(TAG, "subscribeMoviesObserver: ${viewState.movieDetailFields.movie}")
                Log.d(TAG, "subscribeMoviesObserver: ${viewState.movieDetailFields.movieImages}")
            }

        })

        /**
         *
         *
         * Here we listen to number of jobs
         * If There is currently jobs running, keep display the progress bar
         *
         *
         * */
        detailViewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->
            uiCommunicationListener.displayProgressBar(detailViewModel.areAnyJobsActive())
        })


        /**
         *
         *
         * Here we listen to the state of the messages
         * If There is a message, we send it to uiCommunicationListener inside main activity
         * It's handled in the main activity, show dialog/toast/error/success etc
         *
         *
         * */
        detailViewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            stateMessage?.let {
                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            detailViewModel.clearStateMessage()
                        }

                    }
                )
            }

        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        try {
//            dataStateListener = context as DataStateListener
//        } catch (e: ClassCastException) {
//            Log.d(TAG, "onAttach: $e")
//        }
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

}