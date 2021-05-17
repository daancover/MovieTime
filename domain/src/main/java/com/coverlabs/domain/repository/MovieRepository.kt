package com.coverlabs.domain.repository

import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.OrderBy.NONE
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.model.Sort.DESC

interface MovieRepository {

    suspend fun getTopFiveMovies(): List<Movie>

    suspend fun getGenreList(): List<String>

    suspend fun getMovieDetails(id: Int): MovieDetails

    suspend fun searchMovies(
        title: String = "",
        genre: String = "",
        orderBy: OrderBy = NONE,
        sort: Sort = DESC,
        limit: Int = 0,
        offset: Int = 0
    ): List<Movie>
}