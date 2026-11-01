package fr.bounci.screentimeroulette.ui.feature.game

import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.data.GameEnd
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.domain.data.PlayerToVote
import fr.bounci.screentimeroulette.domain.data.RoundEnd

data class GameUiState(
    val username: String = "",
    val currentPhase: GamePhase = GamePhase.GUESSING,

    val roomCode: String = "",
    val isHost: Boolean = false,
    val players: List<Player> = emptyList(),

    val roundNumber: Int = 1,
    val totalRounds: Int = 5,
    val mysteryApp: AppUsage? = null,
    val candidates: List<PlayerToVote> = emptyList(),
    val timeRemaining: Int = 10,
    val hasVoted: Boolean = false,

    val lastRoundResult: RoundEnd? = null,
    val finalGameResult: GameEnd? = null
)