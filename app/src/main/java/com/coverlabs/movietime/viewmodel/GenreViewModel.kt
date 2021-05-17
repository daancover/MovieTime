package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class GenreViewModel(
    private val movieRepository: MovieRepository,
    storageRepository: StorageRepository
) : MovieListViewModel(storageRepository) {

    private var orderBy: OrderBy = OrderBy.NONE
    private var sort: Sort = Sort.DESC

    private val movieList = StateMutableLiveData<List<Movie>>()

    private val searchError = ErrorHandler { error ->
        movieList.postError(error)
    }

    fun onMovieListResult() = movieList.toLiveData()

    fun updateSortingPreferences(orderBy: OrderBy = OrderBy.NONE, sort: Sort = Sort.DESC) {
        this.orderBy = orderBy
        this.sort = sort
    }

    fun searchMovie(genre: String) {
        movieList.postLoading()
        viewModelScope.launch(searchError.handler) {
            val movies = movieRepository.searchMovies(genre = genre, orderBy = orderBy, sort = sort)
            movieList.postSuccess(movies)
        }
    }
}