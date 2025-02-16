package com.moksh.imposterai.presentation.game_viewmodel

import com.moksh.imposterai.data.entity.UserEntity

data class MatchState(
    val matchId: String = "",
    val opponent: UserEntity? = null,
    val currentPlayer: UserEntity? = null,
    val currentTyper: String = "",
) {
    val isMyTurn = currentTyper == currentPlayer?.id
}
