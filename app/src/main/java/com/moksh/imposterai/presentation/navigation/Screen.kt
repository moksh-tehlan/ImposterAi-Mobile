package com.moksh.imposterai.presentation.navigation

import kotlinx.serialization.Serializable


sealed interface Routes {

    @Serializable
    data object Login : Routes

    @Serializable
    data object Signup : Routes

    @Serializable
    data object Home : Routes

    @Serializable
    data object MatchMaking : Routes

    @Serializable
    data object Chat : Routes
}

sealed interface Graphs {

    @Serializable
    data object AuthGraph : Graphs

    @Serializable
    data object HomeGraph : Graphs
}