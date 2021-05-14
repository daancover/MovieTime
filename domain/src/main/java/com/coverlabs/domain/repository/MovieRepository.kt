package com.coverlabs.domain.repository

import com.coverlabs.domain.model.Movie

interface MovieRepository {

    suspend fun getTopFiveMovies(): List<Movie>
}