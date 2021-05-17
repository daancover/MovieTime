package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.OrderBy.NONE
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.model.Sort.DESC
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository,
    storageRepository: StorageRepository
) : MovieListViewModel(storageRepository) {

    private var orderBy: OrderBy = NONE
    private var sort: Sort = DESC

    private val movieList = StateMutableLiveData<List<Movie>>(true)
    private val genreList = StateMutableLiveData<List<String>>(true)

    private val searchError = ErrorHandler { error ->
        movieList.postError(error)
    }

    private val genreListError = ErrorHandler { error ->
        genreList.postError(error)
    }

    fun onMovieListResult() = movieList.toLiveData()

    fun onGenreListResult() = genreList.toLiveData()

    fun updateSortingPreferences(orderBy: OrderBy = NONE, sort: Sort = DESC) {
        this.orderBy = orderBy
        this.sort = sort
    }

    @OnLifecycleEvent(ON_CREATE)
    fun getAllMovies() {
        searchMovie()
    }

    fun searchMovie(
        title: String = "",
        genre: String = ""
    ) {
        viewModelScope.launch(searchError.handler) {
            movieList.postLoading()
            val movies = movieRepository.searchMovies(title, genre, orderBy, sort)
            movieList.postSuccess(movies)
        }
    }

    fun getGenreList() {
        viewModelScope.launch(genreListError.handler) {
            genreList.postLoading()
            val genres = genreList.value?.data ?: movieRepository.getGenreList()
            genreList.postSuccess(genres)
        }
    }
}