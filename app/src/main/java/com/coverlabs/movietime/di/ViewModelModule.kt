package com.coverlabs.movietime.di

import com.coverlabs.movietime.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MovieDetailViewModel(
            movieRepository = get()
        )
    }

    viewModel {
        HomeViewModel(
            movieRepository = get()
        )
    }

    viewModel {
        GenreListViewModel(
            movieRepository = get()
        )
    }

    viewModel {
        SearchViewModel(
            movieRepository = get()
        )
    }

    viewModel {
        GenreViewModel(
            movieRepository = get()
        )
    }
}
