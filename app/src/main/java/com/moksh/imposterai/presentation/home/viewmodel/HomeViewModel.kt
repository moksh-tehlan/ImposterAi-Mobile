package com.moksh.imposterai.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.imposterai.domain.repository.GameRepository
import com.moksh.imposterai.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()
        .onStart { }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            HomeState()
        )

    private val _homeSharedFlow = MutableSharedFlow<HomeEvent>()
    val homeSharedFlow = _homeSharedFlow.asSharedFlow()

    fun findMatch() {
        _homeState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = gameRepository.findMatch()
            when (result) {
                is Result.Success -> {
                    _homeState.update { it.copy(isLoading = false) }
                    _homeSharedFlow.emit(HomeEvent.FindMatchInitiated)
                    Log.i("HomeViewModel", "findMatch: Success")
                }

                is Result.Error -> {
                    _homeState.update { it.copy(isLoading = false) }
                    _homeSharedFlow.emit(HomeEvent.Error(message = result.error.name))
                    Log.i("HomeViewModel", result.error.name)
                }
            }
        }
    }
}