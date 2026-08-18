package com.neohear.data.keystore

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object PassphraseManager {

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "nehear_db_key"
    private const val AES_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val AES_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val AES_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$AES_ALGORITHM/$AES_MODE/$AES_PADDING"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val PREFS_NAME = "nehear_keystore_prefs"
    private const val PREF_ENCRYPTED_PASSPHRASE = "encrypted_passphrase"
    private const val PREF_IV = "encryption_iv"
    private const val PASSPHRASE_LENGTH = 32

    fun getPassphrase(context: Context): ByteArray {
        val prefs = getPrefs(context)
        val existing = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null)
        if (existing != null) {
            return decrypt(context, existing, prefs)
        }
        val passphrase = generatePassphrase()
        val encrypted = encrypt(context, passphrase, prefs)
        prefs.edit().putString(PREF_ENCRYPTED_PASSPHRASE, encrypted).apply()
        return passphrase
    }

    private fun generatePassphrase(): ByteArray {
        val bytes = ByteArray(PASSPHRASE_LENGTH)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        keyStore.getEntry(KEY_ALIAS, null)?.let {
            return (it as KeyStore.SecretKeyEntry).secretKey
        }
        val keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(AES_MODE)
            .setEncryptionPaddings(AES_PADDING)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(context: Context, plaintext: ByteArray, prefs: SharedPreferences): String {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext)

        prefs.edit()
            .putString(PREF_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()

        return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
    }

    private fun decrypt(context: Context, encryptedBase64: String, prefs: SharedPreferences): ByteArray {
        val key = getOrCreateKey()
        val iv = Base64.decode(prefs.getString(PREF_IV, "")!!, Base64.NO_WRAP)
        val ciphertext = Base64.decode(encryptedBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(ciphertext)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
