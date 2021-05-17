package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.movietime.viewmodel.base.BaseViewModel
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class GenreListViewModel(private val movieRepository: MovieRepository) : BaseViewModel() {

    private val genreList = StateMutableLiveData<List<String>>(true)

    private val genreListError = ErrorHandler { error ->
        genreList.postError(error)
    }

    fun onGenreListResult() = genreList.toLiveData()

    @OnLifecycleEvent(ON_CREATE)
    fun getGenreList() {
        viewModelScope.launch(genreListError.handler) {
            genreList.postLoading()
            val genres = genreList.value?.data ?: movieRepository.getGenreList()
            genreList.postSuccess(genres)
        }
    }
}