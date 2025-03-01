package com.moksh.imposterai.data.respository

import com.moksh.imposterai.data.api.AuthApi
import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.entity.request.AuthRequest
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.utils.safeCall
import com.moksh.imposterai.domain.repository.AuthRepository
import com.moksh.imposterai.domain.utils.DataError
import com.moksh.imposterai.domain.utils.Result
import com.moksh.imposterai.domain.utils.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPref: SharedPreferencesManager
) : AuthRepository {
    override suspend fun login(authRequest: AuthRequest): Result<UserEntity, DataError> {
        val result = safeCall { authApi.login(authRequest) }
        return result.map { response ->
            val accessToken = response.data.accessToken
            val refreshToken = response.data.refreshToken

            val user = response.data.userDto
            sharedPref.saveUser(user)
            sharedPref.saveTokens(accessToken, refreshToken)
            response.data.userDto
        }
    }

    override suspend fun signup(authRequest: AuthRequest): Result<UserEntity, DataError> {
        val result = safeCall { authApi.signup(authRequest) }
        return result.map { response ->
            val accessToken = response.data.accessToken
            val refreshToken = response.data.refreshToken

            val user = response.data.userDto
            sharedPref.saveUser(user)
            sharedPref.saveTokens(accessToken, refreshToken)
            response.data.userDto
        }
    }
}