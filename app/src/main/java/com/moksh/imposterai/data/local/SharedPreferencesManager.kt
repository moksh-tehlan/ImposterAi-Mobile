package com.moksh.imposterai.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moksh.imposterai.data.entity.UserEntity

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
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

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }

    val isLoggedIn: Boolean = getToken() != null


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

}