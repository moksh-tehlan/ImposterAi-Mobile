package com.moksh.imposterai.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.data.entity.request.AuthRequest
import com.moksh.imposterai.domain.repository.AuthRepository
import com.moksh.imposterai.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()
        .onStart { }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            LoginState()
        )

    private val _loginSharedFlow = MutableSharedFlow<LoginEvent>()
    val loginSharedFlow = _loginSharedFlow.asSharedFlow()


    private val _signupState = MutableStateFlow(SignupState())
    val signupState = _signupState.asStateFlow()
        .onStart { }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            SignupState()
        )

    private val _signUpSharedFlow = MutableSharedFlow<SignupEvent>()
    val signUpSharedFlow = _signUpSharedFlow.asSharedFlow()

    fun onLoginUsernameChange(username: String) {
        _loginState.value = _loginState.value.copy(username = username)
    }

    fun onSignupUsernameChange(username: String) {
        _signupState.value = _signupState.value.copy(username = username)
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.value = _loginState.value.copy(password = password)
    }


    fun onSignupPasswordChange(password: String) {
        _signupState.value = _signupState.value.copy(password = password)
    }

    fun onSignupConfirmPasswordChange(password: String) {
        _signupState.value = _signupState.value.copy(confirmPassword = password)
    }

    fun onLoginClick() {
        _loginState.value = _loginState.value.copy(isLoading = true)
        viewModelScope.launch {
            val authRequest = AuthRequest(
                username = _loginState.value.username,
                password = _loginState.value.password
            )
            Log.d("AuthViewModel", "Login Request: $authRequest")
            when (val result = authRepository.login(authRequest)) {
                is Result.Error -> {
                    _loginSharedFlow.emit(LoginEvent.LoginError(message = result.error.toString()))
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                    )
                }

                is Result.Success -> {

                    _loginSharedFlow.emit(LoginEvent.LoginSuccessful)
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onSignupClick() {
        _signupState.value = _signupState.value.copy(isLoading = true)
        viewModelScope.launch {
            if (_signupState.value.password != _signupState.value.confirmPassword) {
                _signUpSharedFlow.emit(SignupEvent.SignupError("Password and confirm password do not match"))
                _signupState.value = _signupState.value.copy(isLoading = false)
                return@launch
            }
            val authRequest = AuthRequest(
                username = _signupState.value.username,
                password = _signupState.value.password,
            )
            when (val result = authRepository.signup(authRequest)) {
                is Result.Success -> {
                    _signUpSharedFlow.emit(SignupEvent.SignupSuccessful)
                    _signupState.value = _signupState.value.copy(
                        isLoading = false,
                    )
                }

                is Result.Error -> {
                    _signUpSharedFlow.emit(SignupEvent.SignupError(message = result.error.toString()))
                    _signupState.value = _signupState.value.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }
}