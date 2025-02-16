package com.moksh.imposterai.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.moksh.imposterai.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    fun switchButtonState() {
        _homeState.update {
            it.copy(buttonLoading = !it.buttonLoading)
        }
    }
}