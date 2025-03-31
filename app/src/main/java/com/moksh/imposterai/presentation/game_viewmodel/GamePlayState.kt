package com.moksh.imposterai.presentation.game_viewmodel

data class GamePlayState(
    val gamePhase: GamePhase = GamePhase.NotStarted,
)

sealed interface GamePhase {
    data object NotStarted : GamePhase
    data object MatchMaking : GamePhase
    data object InProgress : GamePhase
    data object GameOver : GamePhase
    data class Guessed(val isCorrectGuess: Boolean, val opponentType: OpponentType) : GamePhase
    data object PlayerLeft : GamePhase
}

sealed interface OpponentType {
    data object AIBot : OpponentType
    data object Human : OpponentType
}