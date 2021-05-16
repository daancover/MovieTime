package com.coverlabs.movietime.di

import com.coverlabs.movietime.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MovieDetailViewModel(
            movieRepository = get(),
            storageRepository = get()
        )
    }

    viewModel {
        HomeViewModel(
            movieRepository = get(),
            storageRepository = get()
        )
    }

    viewModel {
        GenreListViewModel(
            movieRepository = get()
        )
    }

    viewModel {
        SearchViewModel(
            movieRepository = get(),
            storageRepository = get()
        )
    }

    viewModel {
        GenreViewModel(
            movieRepository = get(),
            storageRepository = get()
        )
    }
}
