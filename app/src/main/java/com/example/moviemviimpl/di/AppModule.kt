package com.example.moviemviimpl.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideString(): String {
        return "this is example"
    }
}

