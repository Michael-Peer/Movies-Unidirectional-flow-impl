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
import androidx.viewpager2.widget.ViewPager2
import com.example.moviemviimpl.R
import com.example.moviemviimpl.adapters.ImageSliderAdapter
import com.example.moviemviimpl.adapters.OnPlayButtonClickListener
import com.example.moviemviimpl.state.DetailScreenStateEvent
import com.example.moviemviimpl.utils.*
import com.example.moviemviimpl.viewmodels.DetailViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.youtube.player.YouTubeStandalonePlayer
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class DetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(), OnPlayButtonClickListener {
    private val TAG = "DetailFragment"

    private lateinit var uiCommunicationListener: UICommunicationListener

    private lateinit var moviesImageAdapter: ImageSliderAdapter


    private lateinit var viewPager: ViewPager2

    private var youTubeTrailerId: String? = null


    private val args: DetailFragmentArgs by navArgs()

    private val detailViewModel: DetailViewModel by viewModels {
        viewModelFactory
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

        setViewPager(view)

        setMovieData()

        setupAppBar(view)

        subscribeMoviesObserver()

        setStateEvents()

    }

    private fun setMovieData() {
        detailViewModel.setMovieData(args.Movie)
    }

    private fun setViewPager(view: View) {
        viewPager = view.findViewById(R.id.pager)
        moviesImageAdapter = ImageSliderAdapter(this)
        viewPager.adapter = moviesImageAdapter
    }

    private fun setStateEvents() {
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieImages)
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieTrailer)
    }

    private fun setupAppBar(view: View) {
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_de)
        val navHostFragment = NavHostFragment.findNavController(this)
        val collapsingToolbarLayout =
            view.findViewById<CollapsingToolbarLayout>(R.id.coolaps_tool_bar)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        textView11.text = args.Movie.title
        collapsingToolbarLayout.title = args.Movie.title
    }

    override fun onPlayButtonClick(position: Int) {
        Log.d(TAG, "BUTTON:  CLICKED")
        youTubeTrailerId?.let {
            launchMovieTrailer()
        }
    }

    private fun launchMovieTrailer() {
        val intent = YouTubeStandalonePlayer.createVideoIntent(
            activity,
            Constants.YOUTUBE_API_KEY,
            youTubeTrailerId
        )
        startActivity(intent)
    }

    private fun subscribeMoviesObserver() {
        detailViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                //set fields
                Log.d(TAG, "subscribeMoviesObserver: ${viewState.movieDetailFields.movie}")
                Log.d(TAG, "subscribeMoviesObserver: ${viewState.movieDetailFields.movieImages}")
                viewState.movieDetailFields.movieImages?.backdrops?.let {
                    moviesImageAdapter.submitList(
                        it
                    )
//                    moviesImageAdapter.setItem(
//                        it
//                    )
                }

                viewState.movieDetailFields.movieTrailers?.let {
                    Log.d(TAG, "movieDetailFields: movieTrailers papapa ${it.trailers?.size} ")
                    youTubeTrailerId = detailViewModel.extractTrailer()
                    Log.d(TAG, "subscribeMoviesObserver: youTubeTrailerId $youTubeTrailerId")
                    if (youTubeTrailerId == null) {
                        uiCommunicationListener.onResponseReceived(
                            response = Response(
                                message = "There is no trailers to this movie",
                                uiComponentType = UIComponentType.Toast,
                                messageType = MessageType.Error
                            ),
                            stateMessageCallback = object : StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    detailViewModel.clearStateMessage()
                                }

                            }
                        )
                    }

                }
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


