package com.example.moviemviimpl.di

import com.example.moviemviimpl.BaseApplication
import com.example.moviemviimpl.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
        MovieFragmentFactoryModule::class
    ]
)

interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)

//    fun inject(mainFragment: MainFragment)

}

