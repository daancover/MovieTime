package com.coverlabs.data.storage

import com.coverlabs.data.extension.decodeBase64

class NativeLibraryHandler(private val encryptionHandler: EncryptionHandler) {

    init {
        System.loadLibrary(NATIVE_LIB)
    }

    private external fun a(): String

    external fun b(): String

    private fun getA(): ByteArray {
        return a().decodeBase64().reversedArray()
    }

    fun get(value: String): String {
        return encryptionHandler.get(value, getA())
    }

    fun set(value: String): String {
        return encryptionHandler.set(value, getA())
    }

    companion object {
        private const val NATIVE_LIB = "native-lib"
    }
}