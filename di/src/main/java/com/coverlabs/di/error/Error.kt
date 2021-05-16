package com.coverlabs.di.error

sealed class Error {
    class GenericError() : Error()
    class DeviceNotConnected() : Error()
    open class CustomError() : Error()
}
