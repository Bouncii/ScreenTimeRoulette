package fr.bounci.screentimeroulette.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.bounci.screentimeroulette.ui.feature.game.GameScreen
import fr.bounci.screentimeroulette.ui.feature.home.HomeScreen
import fr.bounci.screentimeroulette.ui.feature.join.JoinScreen
import fr.bounci.screentimeroulette.ui.feature.lobby.LobbyScreen
import fr.bounci.screentimeroulette.ui.session.GameViewModel

@Composable
fun NavHost(
    gameViewModel: GameViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(Destination.Home)
    val uiState by gameViewModel.uiState.collectAsState()

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            // Home
            entry<Destination.Home> {
                HomeScreen(
                    savedUsername = uiState.username,
                    onCreateClick = { username ->
                        gameViewModel.updateUsername(username)
                        gameViewModel.saveCurrentUsername()

                        gameViewModel.createRoom(
                            onSuccess = { code ->
                                backStack.add(Destination.Lobby(code))
                            }
                        )
                    },
                    onJoinClick = { username ->
                        gameViewModel.updateUsername(username)
                        gameViewModel.saveCurrentUsername()
                        backStack.add(Destination.Join)
                    }
                )
            }

            // Join
            entry<Destination.Join> {
                JoinScreen(
                    username = uiState.username,
                    onJoinRoomSuccess = { code ->
                        gameViewModel.joinRoom(
                            code = code,
                            onSuccess = { backStack.add(Destination.Lobby(code)) }
                        )
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // Lobby
            entry<Destination.Lobby> { destination ->
                androidx.compose.runtime.LaunchedEffect(destination.roomCode) {
                    gameViewModel.gameStartedEvent.collect {
                        // Si on reçois le signal gameStarted on change d'entry
                        backStack.add(Destination.Game(destination.roomCode))
                    }
                }

                LobbyScreen(
                    roomCode = uiState.roomCode,
                    players = uiState.players,
                    isHost = uiState.isHost,
                    onGameStart = {
                        gameViewModel.launchGame()
                    },
                    onBackClick = {
                        gameViewModel.resetState()
                        backStack.removeLastOrNull()
                    }
                )
            }

            // Game
            entry<Destination.Game> {
                GameScreen(
                    viewModel = gameViewModel,
                    onHomeClick = {
                        backStack.clear()
                        backStack.add(Destination.Home)
                        gameViewModel.resetState()
                    },
                    onPlayAgainClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}