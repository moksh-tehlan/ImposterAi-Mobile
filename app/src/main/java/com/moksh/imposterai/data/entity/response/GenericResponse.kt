package com.moksh.imposterai.data.entity.response

data class GenericResponse<T>(
    val status: String,
    val data: T,
    val message: String,
    val timestamp: String,
)
