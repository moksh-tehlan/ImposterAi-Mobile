package com.moksh.imposterai.data.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.moksh.imposterai.data.entity.UserEntity

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "ImposterAI",
        createMasterKey(context),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun createMasterKey(context: Context): MasterKey {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return MasterKey.Builder(context)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()
    }

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER = "user"
    }

    fun saveUser(userEntity: UserEntity) {
        val jsonUserEntity = Gson().toJson(userEntity)
        sharedPreferences.edit().putString(USER, jsonUserEntity.toString()).apply()
    }

    fun getUser(): UserEntity? {
        val jsonUserEntity = sharedPreferences.getString(USER, null)
        return Gson().fromJson(jsonUserEntity, UserEntity::class.java)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().putString(ACCESS_TOKEN, accessToken)
            .putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun getAccessTokenToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, null)
    }

    val isLoggedIn: Boolean = getAccessTokenToken() != null


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

}