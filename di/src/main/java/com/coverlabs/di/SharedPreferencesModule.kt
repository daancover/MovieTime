package com.coverlabs.di

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.DEFAULT_MASTER_KEY_ALIAS
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val sharedPreferencesModule = module {
    single {
        provideSettingsPreferences(androidApplication())
    }
}

private const val PREFERENCES_FILE_KEY = "com.coverlabs.authenticator.preferences"

private fun provideSettingsPreferences(app: Application): SharedPreferences {
    val masterKey = MasterKey.Builder(app, DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    return EncryptedSharedPreferences.create(
        app.applicationContext,
        PREFERENCES_FILE_KEY,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
