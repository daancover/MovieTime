package com.coverlabs.movietime.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coverlabs.di.error.Error

class StateMutableLiveData<T>(
    private var isSingleEvent: Boolean = false
) : MutableLiveData<State<T>>() {

    var state = State<T>(isSingleEvent)
        private set

    fun toLiveData(): LiveData<State<T>> = this

    fun postLoading() {
        state.loading()
        postValue(state)
    }

    fun postSuccess(data: T) {
        state.success(data)
        postValue(state)
    }

    fun postComplete() {
        postValue(state.apply {
            complete()
        })
    }

    fun postError(error: Error) {
        postValue(state.apply {
            error(error)
        })
    }

    fun setSingleEvent(value: Boolean) {
        isSingleEvent = value
    }
}