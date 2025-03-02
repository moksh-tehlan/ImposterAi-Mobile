package com.moksh.imposterai.data.api

import com.moksh.imposterai.data.entity.request.AuthRequest
import com.moksh.imposterai.data.entity.request.RefreshTokenRequest
import com.moksh.imposterai.data.entity.response.GenericResponse
import com.moksh.imposterai.data.entity.response.RefreshTokenResponse
import com.moksh.imposterai.data.entity.response.SignupResponse
import com.moksh.imposterai.data.entity.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(
        @Body authRequest: AuthRequest
    ): Response<GenericResponse<UserResponse>>

    @POST("/auth/signup")
    suspend fun signup(
        @Body authRequest: AuthRequest
    ): Response<GenericResponse<SignupResponse>>

    @POST("/auth/refresh-token")
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<GenericResponse<RefreshTokenResponse>>
}