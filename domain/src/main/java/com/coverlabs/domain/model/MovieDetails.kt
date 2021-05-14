package com.coverlabs.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<String>,
    val posterPath: String?,
    val overview: String,
    val cast: List<Cast>,
    val director: Director
)
