package com.coverlabs.domain.repository

import com.coverlabs.domain.model.Movie

interface StorageRepository {

    fun getFavorites(): List<Movie>

    fun addFavorite(movie: Movie)

    fun removeFavorite(movie: Movie)

    fun setFavorites(movieList: List<Movie>)

    fun isFavorite(movie: Movie): Boolean
}