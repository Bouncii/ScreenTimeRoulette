package fr.bounci.screentimeroulette.remote

import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.data.GameEnd
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.domain.data.PlayerToVote
import fr.bounci.screentimeroulette.domain.data.Round
import fr.bounci.screentimeroulette.domain.data.RoundEnd
import fr.bounci.screentimeroulette.domain.data.VoteResult
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAppUsageData(
    val name: String,
    val timeInMinutes: Int,
    val opens: Int
) {
    fun parse() = AppUsage(name, timeInMinutes, opens)
}
@Serializable
data class RemotePlayerJoinedData(
    val roomId: String,
    val hostId: String,
    val joinedPlayer: RemotePlayer,
    val players: List<RemotePlayer>
)
@Serializable
data class RemotePlayer(
    val id: String,
    val pseudo: String,
    val score: Int = 0,
    val isDisconnected: Boolean = false,
    val isHost: Boolean = false
) {
    fun parse() = Player(id, pseudo, score, isDisconnected, isHost)
}

@Serializable
data class RemoteCandidate(
    val id: String,
    val pseudo: String
) {
    fun parse() = PlayerToVote(id, pseudo)
}

@Serializable
data class RemoteRoundData(
    val roundNumber: Int,
    val totalRounds: Int,
    val screenData: List<RemoteAppUsageData>,
    val candidates: List<RemoteCandidate>,
    val duration: Int
) {
    fun parse() = Round(
        roundNumber = roundNumber,
        totalRounds = totalRounds,
        screenData = screenData.map { it.parse() },
        candidates = candidates.map { it.parse() },
        duration = duration
    )
}

@Serializable
data class RemoteGuessResult(
    val guessedId: String,
    val guessedPseudo: String,
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val timeToGuess: Double
) {
    fun parse() = VoteResult(guessedId, guessedPseudo, isCorrect, pointsEarned, timeToGuess)
}

@Serializable
data class RemoteRoundEndData(
    val roundNumber: Int,
    val totalRounds: Int,
    val mysteryPlayer: RemoteCandidate,
    val guesses: Map<String, RemoteGuessResult>,
    val leaderboard: List<RemotePlayer>
) {
    fun parse() = RoundEnd(
        roundNumber = roundNumber,
        totalRounds = totalRounds,
        mysteryPlayer = mysteryPlayer.parse(),
        guesses = guesses.mapValues { it.value.parse() },
        leaderboard = leaderboard.map { it.parse() }
    )
}

@Serializable
data class RemoteGameEndData(
    val roomId: String,
    val totalRounds: Int,
    val podium: List<RemotePlayer>
) {
    fun parse() = GameEnd(
        roomId = roomId,
        totalRounds = totalRounds,
        podium = podium.map { it.parse() }
    )
}

@Serializable
data class RemoteCreateRoomPayload(
    val pseudo: String,
    val screenData: List<RemoteAppUsageData>
)
@Serializable
data class RemoteJoinRoomPayload(
    val roomCode: String,
    val pseudo: String,
    val screenData: List<RemoteAppUsageData>
)

@Serializable
data class RemoteGuessPayload(
    val guessedPlayerId: String
)

@Serializable
data class RemoteSocketResponse(
    val success: Boolean,
    val roomCode: String? = null,
    val message: String? = null
)