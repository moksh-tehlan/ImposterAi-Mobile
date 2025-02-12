package com.moksh.imposterai.presentation.auth.viewmodel

data class LoginState(
    val username: String = "moksh2",
    val password: String = "Moxtehlan@2",
    val isLoading: Boolean = false,
)

data class SignupState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
)
