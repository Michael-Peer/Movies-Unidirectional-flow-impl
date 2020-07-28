package com.example.moviemviimpl.utils

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviemviimpl.repository.DetailRepository
import com.example.moviemviimpl.repository.MainRepository
import com.example.moviemviimpl.viewmodels.DetailViewModel
import com.example.moviemviimpl.viewmodels.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class ViewModelFactory
@Inject
constructor(
    private val mainRepositoryImpl: MainRepository,
    private val detailRepositoryImpl: DetailRepository,
    private val sharedPreferences: SharedPreferences,
    private val sharedPreferencesEditor: SharedPreferences.Editor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> {
                MainViewModel(
                    mainRepository = mainRepositoryImpl,
                    sharedPreferences = sharedPreferences,
                    sharedPreferencesEditor = sharedPreferencesEditor
                ) as T
            }

            DetailViewModel::class.java -> {
                DetailViewModel(
                    moviesDetailRepository = detailRepositoryImpl
                ) as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}