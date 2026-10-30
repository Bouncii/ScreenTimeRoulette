package fr.bounci.screentimeroulette.domain.data

data class Round(
    val roundNumber: Int,
    val totalRounds: Int,
    val screenData: List<AppUsage>,
    val candidates: List<PlayerToVote>,
    val duration: Int
)