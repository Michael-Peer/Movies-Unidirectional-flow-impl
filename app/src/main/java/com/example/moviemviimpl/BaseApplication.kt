package com.example.moviemviimpl

import android.app.Application
import com.example.moviemviimpl.di.AppComponent
import com.example.moviemviimpl.di.DaggerAppComponent

class BaseApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }

}