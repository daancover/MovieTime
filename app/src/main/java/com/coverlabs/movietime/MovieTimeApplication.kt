package com.coverlabs.movietime

import android.app.Application
import com.coverlabs.di.dataModule
import com.coverlabs.di.networkModule
import com.coverlabs.di.sharedPreferencesModule
import com.coverlabs.movietime.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MovieTimeApplication)
            modules(
                listOf(
                    viewModelModule,
                    dataModule,
                    networkModule,
                    sharedPreferencesModule
                )
            )
        }
    }

    companion object {
        const val GRID_LAYOUT_COLUMNS = 2
    }
}