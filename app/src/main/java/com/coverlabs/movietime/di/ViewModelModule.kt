package com.coverlabs.movietime.di

import com.coverlabs.movietime.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(
            movieRepository = get()
        )
    }
}
