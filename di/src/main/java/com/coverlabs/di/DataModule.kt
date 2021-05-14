package com.coverlabs.di

import com.coverlabs.data.datasource.MovieDataSource
import com.coverlabs.domain.repository.MovieRepository
import org.koin.dsl.module

@Suppress("USELESS_CAST")
val dataModule = module {

    single {
        MovieDataSource(
            apolloClient = get()
        ) as MovieRepository
    }
}