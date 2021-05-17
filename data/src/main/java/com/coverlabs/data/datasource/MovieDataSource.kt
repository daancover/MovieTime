package com.coverlabs.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.coverlabs.GetGenreListQuery
import com.coverlabs.GetMovieDetailsQuery
import com.coverlabs.GetTopFiveMoviesQuery
import com.coverlabs.SearchMoviesQuery
import com.coverlabs.data.mapper.map
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.repository.MovieRepository

class MovieDataSource(private val apolloClient: ApolloClient) : MovieRepository {

    override suspend fun getTopFiveMovies(): List<Movie> {
        val response = try {
            apolloClient.query(
                GetTopFiveMoviesQuery()
            ).await()
        } catch (e: ApolloException) {
            throw e
        }

        response.data?.movies?.let { returnedMovies ->
            return returnedMovies.map {
                it?.map() ?: throw Exception()
            }
        }

        if (response.hasErrors()) {
            throw Exception()
        }

        throw Exception()
    }

    override suspend fun getGenreList(): List<String> {
        val response = apolloClient.query(
            GetGenreListQuery()
        ).await()

        val genreList = response.data?.genres

        if (genreList == null || response.hasErrors()) {
            throw Exception()
        }

        return genreList
    }

    override suspend fun getMovieDetails(id: Int): MovieDetails {
        val response = apolloClient.query(
            GetMovieDetailsQuery(id = id)
        ).await()

        response.data?.movie?.let { returnedMovie ->
            return returnedMovie.map()
        }

        if (response.hasErrors()) {
            throw Exception()
        }

        throw Exception()
    }

    override suspend fun searchMovies(
        title: String,
        genre: String,
        orderBy: OrderBy,
        sort: Sort,
        limit: Int,
        offset: Int
    ): List<Movie> {
        val response = try {
            apolloClient.query(
                SearchMoviesQuery(
                    Input.optional(title),
                    Input.optional(genre),
                    Input.optional(orderBy.value),
                    Input.optional(com.coverlabs.type.Sort.safeValueOf(sort.name)),
                    Input.optional(limit),
                    Input.optional(offset)

                )
            ).await()
        } catch (e: ApolloException) {
            throw e
        }

        response.data?.movies?.let { returnedMovies ->
            return returnedMovies.map {
                it?.map() ?: throw Exception()
            }
        }

        if (response.hasErrors()) {
            throw Exception()
        }

        throw Exception()
    }
}