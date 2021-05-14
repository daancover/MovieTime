package com.coverlabs.data.datasource

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.coverlabs.GetTopFiveMoviesQuery
import com.coverlabs.data.mapper.toViewMovieList
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository

class MovieDataSource(private val apolloClient: ApolloClient) : MovieRepository {

    override suspend fun getTopFiveMovies(): List<Movie> {
        val response = try {
            apolloClient.query(
                GetTopFiveMoviesQuery(
                    orderBy = Input.optional("voteAverage")
                )
            ).await()
        } catch (e: ApolloException) {
            // handle protocol errors
            throw e
        }

        val movies = response.data?.movies
        if (movies == null || response.hasErrors()) {
            // handle application errors
            return emptyList()
        }

        return movies.toViewMovieList()
    }
}