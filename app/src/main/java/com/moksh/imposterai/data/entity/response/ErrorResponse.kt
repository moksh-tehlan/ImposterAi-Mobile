package com.moksh.imposterai.data.entity.response

data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: Map<String, String>,
)