package com.moksh.imposterai.data.api

import com.moksh.imposterai.data.entity.request.AuthRequest
import com.moksh.imposterai.data.entity.response.GenericResponse
import com.moksh.imposterai.data.entity.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(
        @Body authRequest: AuthRequest
    ): GenericResponse<UserResponse>

    @POST("/auth/signup")
    suspend fun signup(
        @Body authRequest: AuthRequest
    ): GenericResponse<UserResponse>
}