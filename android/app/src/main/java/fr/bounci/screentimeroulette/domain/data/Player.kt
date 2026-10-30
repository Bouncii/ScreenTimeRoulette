package fr.bounci.screentimeroulette.domain.data

data class Player(
    val id: String,
    val pseudo: String,
    val score: Int = 0,
    val isDisconnected: Boolean = false,
    val isHost: Boolean = false
)
