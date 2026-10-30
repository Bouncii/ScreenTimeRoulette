package fr.bounci.screentimeroulette.domain.data

data class RoundEnd(
    val roundNumber: Int,
    val totalRounds: Int,
    val mysteryPlayer: PlayerToVote,
    val votes: Map<String, VoteResult>,
    val leaderboard: List<Player>
)
