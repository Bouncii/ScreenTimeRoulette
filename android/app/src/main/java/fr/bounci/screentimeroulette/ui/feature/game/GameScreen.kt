package fr.bounci.screentimeroulette.ui.feature.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import fr.bounci.screentimeroulette.ui.session.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onHomeClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (state.currentPhase) {
            GamePhase.GUESSING -> {
                GuessView(
                    appUsage = state.mysteryApp,
                    candidates = state.candidates,
                    timeRemaining = state.timeRemaining,
                    hasVoted = state.hasVoted,
                    onVote = { candidateId -> viewModel.submitGuess(candidateId) }
                )
            }
            GamePhase.ROUND_LEADERBOARD -> {
                state.lastRoundResult?.let { roundEnd ->
                    TemporaryLeaderboardView(
                        leaderboard = roundEnd.leaderboard,
                        timeRemaining = state.timeRemaining
                    )
                }
            }
            GamePhase.FINAL_PODIUM -> {
                state.finalGameResult?.let { gameEnd ->
                    FinalLeaderboardView(
                        podium = gameEnd.podium,
                        onHomeClick = onHomeClick,
                        onPlayAgainClick = onPlayAgainClick
                    )
                }
            }
        }
    }
}