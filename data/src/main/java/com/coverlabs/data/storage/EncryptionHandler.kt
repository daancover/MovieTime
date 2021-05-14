package com.coverlabs.data.storage

import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import com.coverlabs.data.extension.decodeBase64
import com.coverlabs.data.extension.toBase64
import java.nio.charset.StandardCharsets.UTF_8
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionHandler {

    fun get(value: String, key: ByteArray? = null): String {
        try {
            decrypt(value, key)?.reversedArray()?.let {
                return String(it, UTF_8)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    fun set(value: String, key: ByteArray? = null): String {
        return try {
            encrypt(value.encodeToByteArray().reversedArray(), key)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }
    }

    @Throws(Exception::class)
    private fun encrypt(data: ByteArray, key: ByteArray? = null): String {
        val keySpec = key?.let {
            SecretKeySpec(key, KEY_ALGORITHM_AES)
        } ?: run {
            getKeystoreKey()
        }

        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(ENCRYPT_MODE, keySpec)
        val iv = cipher.iv
        val ivString = iv.reversedArray().toBase64()
        var result = ivString + IV_SEPARATOR
        val bytes = cipher.doFinal(data)
        result += bytes.reversedArray().toBase64()
        return result
    }

    @Throws(Exception::class)
    private fun decrypt(data: String, key: ByteArray? = null): ByteArray? {
        val split = data.split(IV_SEPARATOR.toRegex())

        if (split.size != 2) {
            throw IllegalArgumentException("Passed data is incorrect. There was no IV specified with it.")
        }

        val keySpec = key?.let {
            SecretKeySpec(key, KEY_ALGORITHM_AES)
        } ?: run {
            getKeystoreKey()
        }

        val iv = split[0].decodeBase64().reversedArray()
        val encodedData = split[1].decodeBase64().reversedArray()

        val ivSpec = key?.let {
            IvParameterSpec(iv)
        } ?: run {
            GCMParameterSpec(128, iv)
        }

        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(DECRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(encodedData)
    }

    private fun getKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val secretKeyEntry = keyStore.getEntry(ALIAS, null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SEPARATOR = ":"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALIAS = "AES_Keystore_Alias"
    }
}