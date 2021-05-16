package com.coverlabs.data.datasource

import android.content.SharedPreferences
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.StorageRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageDataSource(
    private val sharedPreferences: SharedPreferences
) : StorageRepository {

    override fun getFavorites(): List<Movie> {
        val plainValue = sharedPreferences.getString(PREFS_FAVORITE_MOVIES, "")
        val type = object : TypeToken<ArrayList<Movie>>() {}.type
        return Gson().fromJson(plainValue, type) ?: mutableListOf()
    }

    override fun setFavorites(movieList: List<Movie>) {
        val json = Gson().toJson(movieList)
        sharedPreferences.edit().putString(PREFS_FAVORITE_MOVIES, json).apply()
    }

    override fun isFavorite(movie: Movie): Boolean {
        return getFavorites().any { it.id == movie.id }
    }

    override fun addFavorite(movie: Movie) {
        if (!isFavorite(movie)) {
            val favoriteMovies = getFavorites().toMutableList()
            favoriteMovies.add(movie)
            setFavorites(favoriteMovies)
        }
    }

    override fun removeFavorite(movie: Movie) {
        getFavorites().let {
            val favoriteMovies = it.toMutableList()
            getFavoriteIndex(movie).takeIf { index -> index >= 0 }?.let { index ->
                favoriteMovies.removeAt(index)
                setFavorites(favoriteMovies)
            }
        }
    }

    private fun getFavoriteIndex(movie: Movie): Int {
        var movieIndex = -1
        getFavorites().forEachIndexed { index, it ->
            if (it.id == movie.id) {
                movieIndex = index
            }
        }

        return movieIndex
    }

    companion object {
        private const val PREFS_FAVORITE_MOVIES = "prefs_favorite_movies"
    }
}