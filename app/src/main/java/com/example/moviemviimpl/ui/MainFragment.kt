package com.example.moviemviimpl.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.moviemviimpl.R
import com.example.moviemviimpl.state.MainScreenStateEvent
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.DataStateListener
import com.example.moviemviimpl.utils.StateMessageCallback
import com.example.moviemviimpl.utils.UICommunicationListener
import com.example.moviemviimpl.viewmodels.MainViewModel
import com.example.resclassex.adapters.MoviesRecyclerViewAdapter
import com.example.resclassex.adapters.OnMovieClickListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

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


@FlowPreview
@ExperimentalCoroutinesApi
class MainFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(), OnMovieClickListener {
    private val TAG = "MainFragment"

    lateinit var uiCommunicationListener: UICommunicationListener


    private val mainViewModel: MainViewModel by viewModels {
        viewModelFactory
    }

//    lateinit var mainViewModel: MainViewModel

    lateinit var dataStateListener: DataStateListener


    //recyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var moviesRecyclerViewAdapter: MoviesRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mainViewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupChannel()



        setHasOptionsMenu(true)

        triggerGetMoviesEvent()

        initRecyclerView()

        subscribeMoviesObserver()


    }

    private fun setupChannel() = mainViewModel.setupChannel()

    private fun initRecyclerView() {
        activity?.let { activity ->
            gridLayoutManager = GridLayoutManager(activity, 3)
            moviesRecyclerViewAdapter = MoviesRecyclerViewAdapter(this)
            movies_recycler_view.layoutManager = gridLayoutManager
            movies_recycler_view.adapter = moviesRecyclerViewAdapter
        }
    }

    private fun subscribeMoviesObserver() {


        mainViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
//                moviesRecyclerViewAdapter.submitList(viewState.movies)
                moviesRecyclerViewAdapter.submitList(viewState.moviesFields.movies)

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
        mainViewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->
            uiCommunicationListener.displayProgressBar(mainViewModel.areAnyJobsActive())
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
        mainViewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            stateMessage?.let {
                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            mainViewModel.clearStateMessage()
                        }

                    }
                )
            }

        })

//        mainViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
//
//            Log.d(TAG, "onViewCreated: DataState $dataState ")
//
//            /**
//             *
//             * Handle Error & Loading in the main activity
//             *
//             */
//            dataStateListener.onDataStateChange(dataState)
//
//
//            /**
//             *
//             * Handle Data
//             *
//             */
//            dataState.data?.let { mainScreenViewStateEvent ->
//                mainScreenViewStateEvent.getContentIfNotHandled()?.let { mainViewState ->
//                    mainViewState.movies?.let { movies ->
//                        //set movies data
//                        mainViewModel.setMoviedData(movies)
//                    }
//                }
//
//
//            }
//
//
//        })
//
//        mainViewModel.viewState.observe(viewLifecycleOwner, Observer {
//
//            Log.d(TAG, "inside viewState observer $it")
//
//            it.movies?.let { movies ->
//                //set movies to recycler view
//                moviesRecyclerViewAdapter.submitList(movies)
//            }
//        })
    }

    fun triggerGetMoviesEvent() {
        mainViewModel.setStateEvent(MainScreenStateEvent.GetAllMovies)
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

    override fun onMovieClick(position: Int) {
        Log.d(TAG, "onMovieClick: ${position}")
        val movie = moviesRecyclerViewAdapter.getCurrentItem(position)
        movie?.let {
            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }

//            viewModel.setBlogPost(item)
//            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)


    }

    /**
     *
     * Inflate the menu
     *
     * */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
    }

    /**
     *
     * Whem item from the menu has been selected
     *
     * **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_menu -> {
                Log.d(TAG, "onOptionsItemSelected: order filter menu selected")
                showFilterAndOrderDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     *
     *Inflate the filter and order dialog -
     *
     * **/
    private fun showFilterAndOrderDialog() {
        activity?.let {
            //passing the activity context to the dialog
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_filter_and_order_dialog) //custom layout for the dialog

            val view = dialog.getCustomView()


            val order = mainViewModel.getOrder()

            /**
             *
             * Here we set the V in the correct check box
             * **/
            view.findViewById<RadioGroup>(R.id.filter_group).apply {
                when (order) {
                    Constants.ORDER_BY_TITLE -> {
                        check(R.id.order_title)
                    }

                    Constants.ORDER_BY_YEAR -> {
                        check(R.id.order_year)
                    }
                }
            }
            /**
             *
             * Here we determine what happen if the "apply" button clicked
             *
             * **/
            view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                Log.d(TAG, "showFilterAndOrderDialog: Applying Filters")

                val newFilter =
                    when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
                        R.id.order_title -> {
                            Constants.ORDER_BY_TITLE
                        }

                        R.id.order_year -> {
                            Constants.ORDER_BY_YEAR
                        }

                        else -> {
                            Constants.ORDER_BY_TITLE
                        }
                    }


                mainViewModel.apply {
                    saveFilterOptions(newFilter) //save the filter in shared prefs
                    setMoviesFilter(newFilter) //set the filter to the view state
                }

                onMovieFilter()

                dialog.dismiss()


            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                Log.d(TAG, "FilterDialog: cancelling filter.")
                dialog.dismiss()
            }

            dialog.show()


        }
    }

    private fun onMovieFilter() {
        resetUI()
    }


    private fun resetUI() {
        movies_recycler_view.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
    }

    /**
     *
     * Here I set the adapter to null because the adapter, hence the recycler view and the constraint layout was leaked whan I navigate to DetailFragment
     *
     */
    override fun onDestroyView() {
        movies_recycler_view.adapter = null
        super.onDestroyView()
    }

}