package com.coverlabs.data.mapper

import com.coverlabs.GetTopFiveMoviesQuery
import com.coverlabs.domain.model.Cast
import com.coverlabs.domain.model.Director
import com.coverlabs.domain.model.Movie

fun List<GetTopFiveMoviesQuery.Movie?>.toViewMovieList(): List<Movie> {
    val list = mutableListOf<Movie>()
    forEach { movie ->
        movie?.let {
            list.add(
                it.toViewMovie()
            )
        }
    }

    return list
}

fun GetTopFiveMoviesQuery.Movie.toViewMovie() = Movie(
    id,
    title,
    voteAverage,
    voteCount,
    genres,
    posterPath,
    overview,
    cast.toViewCastList(),
    director.toViewDirector()
)

fun List<GetTopFiveMoviesQuery.Cast?>.toViewCastList(): List<Cast> {
    val list = mutableListOf<Cast>()
    forEach { artist ->
        artist?.let {
            list.add(
                it.toViewCast()
            )
        }
    }

    return list
}

fun GetTopFiveMoviesQuery.Cast.toViewCast(): Cast = Cast(
    profilePath,
    name,
    character,
    order
)

fun GetTopFiveMoviesQuery.Director.toViewDirector(): Director {
    return Director(id, name)
}