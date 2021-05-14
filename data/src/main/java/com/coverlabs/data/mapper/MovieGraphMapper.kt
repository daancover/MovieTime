package com.coverlabs.data.mapper

import com.coverlabs.GetMovieDetailsQuery
import com.coverlabs.GetTopFiveMoviesQuery
import com.coverlabs.SearchMoviesQuery
import com.coverlabs.domain.model.Cast
import com.coverlabs.domain.model.Director
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails

fun GetTopFiveMoviesQuery.Movie.map() = Movie(
    id,
    posterPath
)

fun GetMovieDetailsQuery.Movie.map() = MovieDetails(
    id,
    title,
    voteAverage,
    voteCount,
    genres,
    posterPath,
    overview,
    cast.map {
        it.map()
    },
    director.map()
)

fun GetMovieDetailsQuery.Cast.map(): Cast = Cast(
    profilePath,
    name,
    character
)

fun GetMovieDetailsQuery.Director.map(): Director {
    return Director(id, name)
}


fun SearchMoviesQuery.Movie.map() = Movie(
    id,
    posterPath
)
