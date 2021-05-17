package com.coverlabs.movietime.viewmodel.base

import com.coverlabs.di.error.Error
import com.coverlabs.movietime.viewmodel.base.State.Status.*

class State<T>(private val isSingleEvent: Boolean = false) {

    private var isEventHandled = false

    val dataIfNotHandled: T?
        get() = if (isSingleEvent && isEventHandled) {
            null
        } else {
            isEventHandled = true
            data
        }

    var status: Status = CREATED
        private set
    var data: T? = null
        private set
    var error: Error? = null
        private set

    fun loading() {
        isEventHandled = false
        this.status = LOADING
        this.data = null
        this.error = null
    }

    fun success(data: T) {
        this.status = SUCCESS
        this.data = data
        this.error = null
    }

    fun complete() {
        this.status = COMPLETED
        this.data = null
        this.error = null
    }

    fun error(error: Error) {
        this.status = ERROR
        this.data = null
        this.error = error
    }

    override fun toString(): String {
        return "status: $status data: $data error: $error"
    }

    enum class Status {
        CREATED,
        LOADING,
        SUCCESS,
        COMPLETED,
        ERROR
    }
}