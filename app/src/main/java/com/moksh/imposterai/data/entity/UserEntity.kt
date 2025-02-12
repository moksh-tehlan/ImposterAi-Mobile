package com.moksh.imposterai.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val username: String,
)
