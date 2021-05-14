package com.coverlabs.domain.repository

import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails

interface MovieRepository {

    suspend fun getTopFiveMovies(): List<Movie>

    suspend fun getGenreList(): List<String>

    suspend fun getMovieDetails(id: Int): MovieDetails

    suspend fun searchMovies(
        title: String = "",
        genre: String = "",
        orderBy: String = ""
    ): List<Movie>
}