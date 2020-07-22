package com.example.moviemviimpl

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemviimpl.utils.DataState
import com.example.moviemviimpl.utils.DataStateListener
import com.example.moviemviimpl.viewmodels.BaseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), DataStateListener {
    private val TAG = "MainActivity"

    lateinit var baseViewModel: BaseViewModel


    @Inject
    lateinit var string: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inject()

//        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

        Log.d(TAG, "onCreate: String $string")
    }


    //for dagger
    private fun inject() {
        (application as BaseApplication).appComponent
            .inject(this@MainActivity)


    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            // Handle loading
            showProgressBar(dataState.loading)

            // Handle Message
            dataState.message?.let { messageEvent ->
                messageEvent.getContentIfNotHandled()?.let {message->
                    showToast(message)
                }

            }
        }
    }

    /**
     * Handle Error
     */

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     *
     *Handle Loading
     *
     */
    fun showProgressBar(isVisible: Boolean) {
        if (isVisible) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

}