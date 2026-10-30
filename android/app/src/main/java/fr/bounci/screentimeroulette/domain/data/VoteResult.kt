package fr.bounci.screentimeroulette.domain.data

data class VoteResult(
    val votedId: String,
    val votedPseudo: String,
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val timeToGuess: Double
)
