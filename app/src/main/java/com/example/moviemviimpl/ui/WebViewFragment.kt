package com.example.moviemviimpl.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.moviemviimpl.R
import kotlinx.android.synthetic.main.fragment_web_view.*


class WebViewFragment : Fragment() {
    private val TAG = "WebViewFragment"

    private val args: WebViewFragmentArgs by navArgs()

    lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: ")
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        web_view.webChromeClient = WebChromeClient()
        webView = web_view

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl("https://en.wikipedia.org/wiki/${args.urlData}")
        }
    }


}