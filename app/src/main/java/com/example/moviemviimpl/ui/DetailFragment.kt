package com.example.moviemviimpl.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviemviimpl.R
import com.example.moviemviimpl.adapters.*
import com.example.moviemviimpl.model.MovieDetail
import com.example.moviemviimpl.state.DetailScreenStateEvent
import com.example.moviemviimpl.utils.*
import com.example.moviemviimpl.viewmodels.DetailViewModel
import com.example.resclassex.adapters.OnMovieClickListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.youtube.player.YouTubeStandalonePlayer
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class DetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(), OnPlayButtonClickListener, OnVideoClickListener, OnMovieClickListener, OnCastClickListener {
    private val TAG = "DetailFragment"

    /**
     *MainActivity communication
     * **/
    private lateinit var uiCommunicationListener: UICommunicationListener


    /**
     * ViewPager
     * **/
    private lateinit var moviesImageAdapter: ImageSliderAdapter
    private lateinit var viewPager: ViewPager2
    private var viewPagerPosition: Int? = null

    /**
     * Video recyclerview
     * **/
    private lateinit var movieVideoRecyclerAdapter: MovieDetailVideosRecyclerAdapter
    private lateinit var movieVideoRecyclerView: RecyclerView
    private lateinit var movieVideoRecyclerViewLayoutManager: LinearLayoutManager

    /**
     * Cast recyclerview
     * **/
    private lateinit var movieCastAdapter: MovieCastAdapter
    private lateinit var movieCastRecyclerView: RecyclerView
    private lateinit var movieCastRecyclerViewLayoutManager: LinearLayoutManager

    /**
     * Similar movies recyclerview
     * **/
    private lateinit var similarMoviesAdapter: SimilarMoviesAdapter
    private lateinit var similarMoviesRecyclerView: RecyclerView
    private lateinit var similarMoviesRecyclerViewLayoutManager: LinearLayoutManager


    /**
     * For "Main" trailer
     * **/
    private var youTubeTrailerId: String? = null


    /**
     * movieID
     * **/
    private val args: DetailFragmentArgs by navArgs()
    private var currentMovieID: Int? = null

    private val detailViewModel: DetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("viewpagerdebug", "onCreate: Befote change")
        savedInstanceState?.let { bundle ->
            (bundle[Constants.VIEW_PAGER_POSITION] as Int)?.let { position ->
                Log.d("viewpagerdebug", "onCreate: Agter change")
                Log.d("viewpagerdebug", "onCreate View Pager Position $position")
                viewPagerPosition = position
                Log.d("viewpagerdebug", "onCreate viewPagerPosition $viewPagerPosition")
            }

            (bundle[Constants.CURRENT_MOVIE_ID] as Int)?.let { movieID ->
                currentMovieID = movieID
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        setViewPager(view)
        Log.d(TAG, "onViewCreated: After setViewPager")

        setRecyclerView(view)
        Log.d(TAG, "onViewCreated: After setRecyclerView")

        if (currentMovieID == null) {
            setMovieData(args.movieID)
            Log.d(TAG, "onViewCreated: After setMovieData")
        }


        setupAppBar(view)
        Log.d(TAG, "onViewCreated: After setupAppBar")

        subscribeMoviesObserver()
        Log.d(TAG, "onViewCreated: After subscribeMoviesObserver")

        setStateEvents()
        Log.d(TAG, "onViewCreated: After setStateEvents")

    }


    private fun setMovieData(movieID: Int) {
//        detailViewModel.setMovieIdData(args.movieID)
        detailViewModel.setMovieIdData(movieID)

    }

    private fun setRecyclerView(view: View) {
        setMovieVideoRecycler(view)
        setMovieCastRecycler(view)
        setSimilarMoviesRecycler(view)
    }

    private fun setSimilarMoviesRecycler(view: View) {
        similarMoviesRecyclerView = view.findViewById(R.id.movie_detail_similar_recyclerview)
        similarMoviesRecyclerViewLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        similarMoviesRecyclerView.layoutManager = similarMoviesRecyclerViewLayoutManager

        similarMoviesRecyclerView.addItemDecoration(SpacingItemDecoration(16))

        similarMoviesAdapter = SimilarMoviesAdapter(this)
        similarMoviesRecyclerView.adapter = similarMoviesAdapter
    }

    private fun setMovieCastRecycler(view: View) {
        movieCastRecyclerView = view.findViewById(R.id.movie_detail_cast_recyclerview)
        movieCastRecyclerViewLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        movieCastRecyclerView.layoutManager = movieCastRecyclerViewLayoutManager

        movieCastRecyclerView.addItemDecoration(SpacingItemDecoration(16))


        movieCastAdapter = MovieCastAdapter(this)
        movieCastRecyclerView.adapter = movieCastAdapter

    }

    private fun setMovieVideoRecycler(view: View) {
        movieVideoRecyclerView = view.findViewById(R.id.movie_detail_video_recyclerview)
        movieVideoRecyclerViewLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        movieVideoRecyclerView.layoutManager = movieVideoRecyclerViewLayoutManager

        movieVideoRecyclerView.addItemDecoration(SpacingItemDecoration(16))


        movieVideoRecyclerAdapter = MovieDetailVideosRecyclerAdapter(this)
        movieVideoRecyclerView.adapter = movieVideoRecyclerAdapter
    }

    private fun setViewPager(view: View) {
        viewPager = view.findViewById(R.id.pager)


        moviesImageAdapter = ImageSliderAdapter(this)
        viewPager.adapter = moviesImageAdapter

        /**
         *
         * If viewPagerPosition not null, meaning there was a configuration change
         * And we need to set the position explicitly
         *
         * Must launch on coroutine
         * TODO: Fix scrolled to position on configuration change
         * **/
        viewPagerPosition?.let {
            lifecycleScope.launch(Dispatchers.Main) {
                viewPager.currentItem = it
                Log.d(TAG, "\"viewpagerdebug\": ${viewPager.currentItem}")
            }

        }


    }

    private fun setStateEvents() {
        //TODO: Multiple time - fix this
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieDetail)
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieImages)
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieTrailer)
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetMovieCredits)
        detailViewModel.setStateEvent(DetailScreenStateEvent.GetSimilarMovies)
    }

    private fun setupAppBar(view: View) {
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_de)
        val navHostFragment = NavHostFragment.findNavController(this)
        val collapsingToolbarLayout =
            view.findViewById<CollapsingToolbarLayout>(R.id.coolaps_tool_bar)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

//        textView11.text = args.Movie.title
//        collapsingToolbarLayout.title = args.Movie.title
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
//                Log.d(
//                    TAG,
//                    "subscribeMoviesObserver: ${viewState.movieDetailFields.movieDetails}"
//                )
//                Log.d(
//                    TAG,
//                    "subscribeMoviesObserver: ${viewState.movieDetailFields.movieImages}"
//                )

                /**
                 *
                 * Setup Slider images
                 *
                 * **/

                viewState.movieDetailFields.movieImages?.backdrops?.let {
                    moviesImageAdapter.submitList(
                        it
                    )

//                    moviesImageAdapter.setItem(
//                        it
//                    )

                }

                /**
                 *
                 * Setup Trailer
                 *
                 * **/

                viewState.movieDetailFields.movieTrailers?.let {
                    movieVideoRecyclerAdapter.submitList(it.trailers)


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

                /**
                 *
                 * Setup Cast
                 *
                 * **/

                viewState.movieDetailFields.movieCredits?.cast?.let {
                    movieCastAdapter.submitList(it)
                }

                /**
                 *
                 * Setup Similar Movies
                 *
                 * **/
                viewState.movieDetailFields.similarMovies?.movies?.let {
//                    Log.d(TAG, "similar movies $it")
                    if (it.isEmpty()) {
//                        Log.d(TAG, "similarMovies $it is empty ")
                        similarMoviesAdapter.setPlaceHolderEmptyData()
                    } else {
//                        Log.d(TAG, "similarMovies $it is not empty ")
                        similarMoviesAdapter.submitList(it)
                    }
                }

                /**
                 *
                 * Setup initial fields
                 *
                 **/

                viewState.movieDetailFields.movieDetails?.let { movieDetails ->
//                    Log.d(TAG, "movieDetailFieldsmovie:$movieDetails ")
                    setupFields(movieDetails)


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

    private fun setupFields(movie: MovieDetail) {
        val currentUrl = "https://image.tmdb.org/t/p/w400${movie.posterPath}"

        Glide.with(this)
            .load(currentUrl)
            .apply(RequestOptions.overrideOf(300, 500))
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(movie_detail_title_poster)


        movie_detail_title_text.text = movie.title
        movie_detail_release_date_text.text = movie.releaseDate
        movie_detail_overview_text.text = movie.overview

        val geners: String = detailViewModel.getGeners(movie.genres)
        movie_detail_genere_text.text = geners

        movie_detail_rating_text.text = movie.voteAverage.toString()
    }

    /**
     *
     * Videos list
     *
     * **/
    override fun onVideoClick(position: Int) {
        val trailerKey = movieVideoRecyclerAdapter.getTrailerKey(position)
        trailerKey?.let {
            youTubeTrailerId = it
            launchMovieTrailer()
        }
    }

    /**
     *
     * Play button in the "main" trailer
     *
     * **/
    override fun onPlayButtonClick(position: Int) {
        Log.d(TAG, "BUTTON:  CLICKED")
        youTubeTrailerId?.let {
            launchMovieTrailer()
        }
    }

    /**
     *
     * Similar Movies list
     *
     * **/
    override fun onMovieClick(position: Int) {
        Log.d(TAG, "onMovieClick: ${position}")
        val movie = similarMoviesAdapter.getCurrentItem(position)
        movie?.id?.let {
            currentMovieID = it
            setMovieData(it)
            setStateEvents()
            resetUI()
        }
    }

    private fun resetUI() {
        viewPagerPosition = 0
        movieVideoRecyclerView.smoothScrollToPosition(0)
        movieCastRecyclerView.smoothScrollToPosition(0)
        similarMoviesRecyclerView.smoothScrollToPosition(0)
        movie_detail_scrollview.fullScroll(ScrollView.FOCUS_UP)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

    /**
     * !IMPORTANT!
     * TODO: Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("viewpagerdebug", "onSaveInstanceState: ")
        val sliderPosition = viewPager.currentItem
        Log.d("viewpagerdebug", "onSaveInstanceState: $sliderPosition")



        /**
         *
         * Position of the "main" slider
         *
         * **/
        outState.putInt(
            Constants.VIEW_PAGER_POSITION,
            sliderPosition
        )


        /**
         * Bug:
         * When we click on movie from similar movies, we call setMovieData to set the new movie.
         * The problem is when we rotate the screen, onViewCreated called again
         * inside onViewCreated we have setMovieData, but with the args.movieID.
         * So on configuration change will always return args.movieID
         *
         * Fix:
         * Check if there is saved movieID in the bundle?
         *
         * **/
        currentMovieID?.let {
            outState.putInt(
                Constants.CURRENT_MOVIE_ID,
                it
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun onCastClick(position: Int) {
        val cast = movieCastAdapter.getCurrentItem(position)
        cast?.name?.let {
            val action = DetailFragmentDirections.actionDetailFragmentToWebViewFragment(it)
            findNavController().navigate(action)
        }

    }


}


