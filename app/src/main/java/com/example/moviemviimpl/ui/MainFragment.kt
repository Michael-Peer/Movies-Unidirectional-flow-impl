package com.example.moviemviimpl.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.moviemviimpl.R
import com.example.moviemviimpl.state.MainScreenStateEvent
import com.example.moviemviimpl.utils.Constants
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

        setupToolbar(view)

        setupChannel()

        triggerGetMoviesEvent()

        initRecyclerView()

        subscribeMoviesObserver()


    }

    /**
     *
     * The "best way" to setup the tool bar is through mainActivity.
     * In this case, I didn't to it because according to the google docs, If your app changes from screen to screen - you should create app bar to each screen
     *
     * **/

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_main)
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
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
                viewState.moviesFields.movies?.let { moviesRecyclerViewAdapter.modifyList(it) }
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

    }

    private fun triggerGetMoviesEvent() {
        mainViewModel.setStateEvent(MainScreenStateEvent.GetAllMovies)
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
             *
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


                /**
                 *
                 *
                 * newFilter = ORDER_BY_TITLE / ORDER_BY_YEAR
                 *
                 * **/
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

                Log.d(TAG, "NEW1 FILTER : $order")
                Log.d(TAG, "NEW2 FILTER : $newFilter")

                /***
                 *
                 * If it is the same, there is no point to make api call and cache call again
                 *  order = the initial order
                 *  newFilter = the filter that the user choose
                 *
                 * **/
                if (order == newFilter) {
                    Log.d(TAG, "showFilterAndOrderDialog: Inside if statment")
                    dialog.dismiss()
                } else {
                    mainViewModel.apply {
                        saveFilterOptions(newFilter) //save the filter in shared prefs
                        setMoviesFilter(newFilter) //set the filter to the view state
                    }

                    onMovieFilter()

                    dialog.dismiss()
                }


            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                Log.d(TAG, "FilterDialog: cancelling filter.")
                dialog.dismiss()
            }

            dialog.show()


        }
    }


    /**
     *
     *
     * ReLoad the page with the right order
     *
     * **/
    private fun onMovieFilter() {
        Log.d(TAG, "onMovieFilter: ")
        mainViewModel.loadOrderedPage().let {
            resetUI()
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
     * I nedded to set registerAdapterDataObserver here because scrolling bug
     * when the first item was in the view and I re-ordered the list, the scrolling followed after the previous 0 position
     * instead of go back to the top of the list
     *
     * **/
    private fun resetUI() {

        moviesRecyclerViewAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                movies_recycler_view.smoothScrollToPosition(0)
//                moviesRecyclerViewAdapter.notifyDataSetChanged()
                moviesRecyclerViewAdapter.notifyItemRangeChanged(fromPosition, toPosition)
            }
        })
//        gridLayoutManager.scrollToPosition(0)
//
//        uiCommunicationListener.hideSoftKeyboard()
//        focusable_view.requestFocus()

    }

    /**
     *
     * When item from the menu has been selected
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
     * Inflate the menu
     *
     * */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
        val menuItem = menu.findItem(R.id.filter_menu_search)
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(TAG, "onQueryTextChange: " + newText);
                moviesRecyclerViewAdapter.filter(newText);

                return false;
            }
        })
    }

    /**
     *
     * Setting the interface
     *
     * **/
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


    /**
     *
     * Here I set the adapter to null because the adapter, hence the recycler view and the constraint layout was leaked whan I navigate to DetailFragment
     *
     * We set here the support action bar to null to prevent leaking:
     * One fragment called  (activity as AppCompatActivity).setSupportActionBar with a view contained in the fragments layout.
     * When switching to another fragment the resources couldn't get gc'ed because the activity was still holding a reference to the no longer visible toolbar.
     *
     * Here we set also addOnAttachStateChangeListener because the animation
     * @stackoverflow question ref: https://stackoverflow.com/questions/35520946/leak-canary-recyclerview-leaking-madapter
     *
     *
     *
     */
    override fun onDestroyView() {
        movies_recycler_view.adapter = null
        (activity as AppCompatActivity).setSupportActionBar(null) //remove
//        movies_recycler_view.addOnAttachStateChangeListener(object :
//            View.OnAttachStateChangeListener {
//            override fun onViewDetachedFromWindow(v: View?) {
//                movies_recycler_view.adapter = null;
//            }
//
//            override fun onViewAttachedToWindow(v: View?) {
//                TODO("Not yet implemented")
//            }
//
//        })
        super.onDestroyView()
    }

}