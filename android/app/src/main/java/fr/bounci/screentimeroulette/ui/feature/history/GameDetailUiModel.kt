package fr.bounci.screentimeroulette.ui.feature.history

data class GameDetailUiModel(
    val dateString: String,
    val playerCount: Int,
    val roundCount: Int,
    val rounds: List<RoundDetailUiModel>
)

data class RoundDetailUiModel(
    val roundNumber: Int,
    val appName: String,
    val timeString: String,
    val opensCount: Int,
    val playerGuesses: List<PlayerGuessUiModel>,
    val correctPlayerName: String
)

data class PlayerGuessUiModel(
    val rank: Int,
    val playerName: String,
    val guessedName: String,
    val pointsEarned: Int
)
