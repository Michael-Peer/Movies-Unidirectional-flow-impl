package com.example.moviemviimpl.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.example.moviemviimpl.repository.DetailRepositoryImpl
import com.example.moviemviimpl.repository.MainRepositoryImpl
import com.example.moviemviimpl.utils.ViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ViewModelModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideViewModelFactory(
        mainRepository: MainRepositoryImpl,
        detailRepository: DetailRepositoryImpl,
        sharedPreferences: SharedPreferences,
        sharedPreferencesEditor: SharedPreferences.Editor
    ): ViewModelProvider.Factory {
        return ViewModelFactory(
            mainRepositoryImpl = mainRepository,
            detailRepositoryImpl = detailRepository,
            sharedPreferences = sharedPreferences,
            sharedPreferencesEditor = sharedPreferencesEditor
        )
    }
}