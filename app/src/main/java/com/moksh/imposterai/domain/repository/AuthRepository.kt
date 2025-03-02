package com.moksh.imposterai.domain.repository

import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.entity.request.AuthRequest
import com.moksh.imposterai.domain.utils.DataError
import com.moksh.imposterai.domain.utils.EmptyResult
import com.moksh.imposterai.domain.utils.Result

interface AuthRepository {
    suspend fun login(authRequest: AuthRequest): Result<UserEntity, DataError>
    suspend fun signup(authRequest: AuthRequest): EmptyResult<DataError>
}