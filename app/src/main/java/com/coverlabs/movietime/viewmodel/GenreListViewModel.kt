package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coverlabs.domain.repository.MovieRepository
import kotlinx.coroutines.launch

class GenreListViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val genreList = MutableLiveData<List<String>>()

    fun onGenreListResult(): LiveData<List<String>> = genreList

    fun getGenreList() {
        viewModelScope.launch {
            val genres = movieRepository.getGenreList()
            genreList.postValue(genres)
        }
    }
}