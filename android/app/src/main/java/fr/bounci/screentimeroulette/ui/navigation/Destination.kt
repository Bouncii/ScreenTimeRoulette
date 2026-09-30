package fr.bounci.screentimeroulette.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

interface Destination : NavKey {

    @Serializable
    data object Home : Destination

    @Serializable
    data object Join : Destination

    @Serializable
    data class Lobby(val roomCode: String) : Destination

    @Serializable
    data class Game(val roomCode: String) : Destination
}