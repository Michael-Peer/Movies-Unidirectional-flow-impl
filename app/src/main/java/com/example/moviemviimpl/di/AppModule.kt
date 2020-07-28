package com.example.moviemviimpl.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.moviemviimpl.BaseApplication
import com.example.moviemviimpl.api.MoviesApi
import com.example.moviemviimpl.cache.Database
import com.example.moviemviimpl.cache.MovieDao
import com.example.moviemviimpl.repository.DetailRepository
import com.example.moviemviimpl.repository.DetailRepositoryImpl
import com.example.moviemviimpl.repository.MainRepository
import com.example.moviemviimpl.repository.MainRepositoryImpl
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.LiveDataCallAdapterFactory
import com.example.moviemviimpl.utils.PreferenceKeys
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideString(): String {
        return "this is example"
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDB(app: BaseApplication): Database {
        return Room
            .databaseBuilder(app, Database::class.java, Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideMoviesDao(db: Database): MovieDao {
        return db.getMoviesDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRetrofitService(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder): MoviesApi {
        return retrofitBuilder
            .build()
            .create(MoviesApi::class.java)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideMainRepository(moviesApi: MoviesApi, moviesDao: MovieDao): MainRepository {
        return MainRepositoryImpl(moviesApi, moviesDao)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDetailRepository(moviesApi: MoviesApi, moviesDao: MovieDao): DetailRepository {
        return DetailRepositoryImpl(moviesApi, moviesDao)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(
        application: BaseApplication
    ): SharedPreferences {
        return application
            .getSharedPreferences(
                PreferenceKeys.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }
}


