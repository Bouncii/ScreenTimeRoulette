package fr.bounci.screentimeroulette.domain.data

data class ServerHealth(
    val status: String,
    val activeRooms: Int,
    val uptime : Double,
)
