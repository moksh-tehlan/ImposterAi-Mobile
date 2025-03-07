package com.moksh.imposterai.data.entity.response

import com.moksh.imposterai.data.entity.UserEntity

data class UserResponse(
    val userDto: UserEntity,
    val accessToken: String,
    val refreshToken: String,
)
