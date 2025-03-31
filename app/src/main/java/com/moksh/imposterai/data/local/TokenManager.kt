package com.moksh.imposterai.data.local

import com.moksh.imposterai.presentation.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    private val _navigationEvent = MutableSharedFlow<Routes>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun triggerLogout() {
        sharedPreferencesManager.clearAll()
        CoroutineScope(Dispatchers.Main).launch { _navigationEvent.emit(Routes.Login) }
    }
}