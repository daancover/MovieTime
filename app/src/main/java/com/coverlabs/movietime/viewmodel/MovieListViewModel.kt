package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.OnLifecycleEvent
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.viewmodel.base.BaseViewModel
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData

open class MovieListViewModel(
    private val storageRepository: StorageRepository
) : BaseViewModel() {

    private val favoriteMovieList = StateMutableLiveData<List<Movie>>(true)

    fun onFavoriteMovieListResult() = favoriteMovieList.toLiveData()

    @OnLifecycleEvent(ON_CREATE)
    fun getFavorites() {
        favoriteMovieList.postLoading()
        storageRepository.getFavorites().let {
            favoriteMovieList.postSuccess(it)
        }
    }

    fun changeFavoriteStatus(isFavorite: Boolean, movie: Movie) {
        if (isFavorite) {
            addFavorite(movie)
        } else {
            removeFavorite(movie)
        }
    }

    private fun addFavorite(movie: Movie) {
        storageRepository.addFavorite(movie)
    }

    private fun removeFavorite(movie: Movie) {
        storageRepository.removeFavorite(movie)
    }

    private fun isFavorite(movie: Movie): Boolean {
        return storageRepository.isFavorite(movie)
    }

    fun isFavoriteList(movieList: List<Movie>): List<Boolean> {
        val isFavorite = mutableListOf<Boolean>()
        movieList.forEach {
            isFavorite.add(isFavorite(it))
        }

        return isFavorite
    }
}