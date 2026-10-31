package fr.bounci.screentimeroulette.domain.data

data class GameEnd(
    val roomId: String,
    val totalRounds: Int,
    val podium: List<Player>
)
