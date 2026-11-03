package fr.bounci.screentimeroulette.domain.data

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val pseudo: String,
    val score: Int = 0,
    val isDisconnected: Boolean = false,
    val isHost: Boolean = false
)
