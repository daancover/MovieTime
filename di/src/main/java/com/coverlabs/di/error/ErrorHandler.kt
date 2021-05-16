package com.coverlabs.di.error

import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.coverlabs.di.error.Error.DeviceNotConnected
import com.coverlabs.di.error.Error.GenericError
import kotlinx.coroutines.CoroutineExceptionHandler

class ErrorHandler(private val listener: (Error) -> Unit) {

    val handler = CoroutineExceptionHandler { _, exception ->
        val error = getError(exception)
        listener(error)
    }

    private fun getError(
        exception: Throwable
    ): Error {
        return (exception as? ApolloException)?.let {
            when (it) {
                is ApolloNetworkException -> DeviceNotConnected()
                else -> GenericError()
            }
        } ?: run {
            GenericError()
        }
    }
}