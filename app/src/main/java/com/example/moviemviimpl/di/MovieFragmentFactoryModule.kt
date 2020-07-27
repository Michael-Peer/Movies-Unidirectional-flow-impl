package com.example.moviemviimpl.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.example.moviemviimpl.utils.MovieFragmentFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Module
object MovieFragmentFactoryModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideMovieFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return MovieFragmentFactory(
           viewModelFactory =  viewModelFactory
        )
    }
}