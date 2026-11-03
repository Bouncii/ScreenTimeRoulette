package fr.bounci.screentimeroulette.domain.data

data class PodiumData(
    val timeString: Long = System.currentTimeMillis(),
    val gameEnd: GameEnd
)
