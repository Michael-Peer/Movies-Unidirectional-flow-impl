package com.example.moviemviimpl.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.example.moviemviimpl.ui.MainFragment
import javax.inject.Inject

class MovieFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MainFragment::class.java.name -> {
                val fragment =
                    MainFragment(viewModelFactory)
                fragment
            }
            else -> {
                super.instantiate(classLoader, className)
            }
        }
    }

}