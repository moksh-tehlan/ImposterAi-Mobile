package com.moksh.imposterai.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moksh.imposterai.data.entity.UserEntity

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

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