package com.coverlabs.di.module

import com.apollographql.apollo.ApolloClient
import com.coverlabs.data.storage.EncryptionHandler
import com.coverlabs.data.storage.NativeLibraryHandler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.koin.dsl.module
import java.util.concurrent.TimeUnit.SECONDS

private const val TIMEOUT = 30L

@Suppress("USELESS_CAST")
val networkModule = module {

    single {
        EncryptionHandler()
    }

    single {
        provideNativeLibraryHandler(
            encryptionHandler = get()
        )
    }

    single {
        provideOkHttpClient()
    }

    single {
        provideApolloClient(
            handler = get(),
            okHttpClient = get()
        )
    }
}

private fun provideNativeLibraryHandler(
    encryptionHandler: EncryptionHandler
): NativeLibraryHandler {
    return NativeLibraryHandler(encryptionHandler)
}

private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .connectTimeout(TIMEOUT, SECONDS)
        .readTimeout(TIMEOUT, SECONDS)
        .writeTimeout(TIMEOUT, SECONDS)
        .build()
}

private fun provideApolloClient(
    handler: NativeLibraryHandler,
    okHttpClient: OkHttpClient
): ApolloClient {
    return ApolloClient.builder()
        .okHttpClient(okHttpClient)
        .serverUrl(handler.get(handler.b()))
        .build()
}