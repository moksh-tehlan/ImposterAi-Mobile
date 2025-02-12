package com.moksh.imposterai.presentation.auth.viewmodel

sealed interface LoginEvent{
    data object LoginSuccessful: LoginEvent
    data class LoginError(val message: String): LoginEvent
}

sealed interface SignupEvent{
    data object SignupSuccessful: SignupEvent
    data class SignupError(val message: String): SignupEvent
}