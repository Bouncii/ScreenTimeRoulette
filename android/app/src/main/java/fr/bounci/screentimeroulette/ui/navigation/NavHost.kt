package fr.bounci.screentimeroulette.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.bounci.screentimeroulette.ui.feature.game.GameScreen
import fr.bounci.screentimeroulette.ui.feature.home.HomeScreen
import fr.bounci.screentimeroulette.ui.feature.join.JoinScreen
import fr.bounci.screentimeroulette.ui.feature.lobby.LobbyScreen
import fr.bounci.screentimeroulette.ui.session.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.usecase.CreateRoom

@Composable
fun NavHost(
    gameViewModel: GameViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(Destination.Home)
    val username by gameViewModel.username.collectAsState()
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            // Home
            entry<Destination.Home> {
                HomeScreen(
                    onCreateClick = { username ->
                        gameViewModel.updateUsername(username)
                        CreateRoom(username, listOf(
                            AppUsage(name = "Instagram", timeInMinutes = 120, opens = 45),
                            AppUsage(name = "TikTok", timeInMinutes = 90, opens = 30),
                            AppUsage(name = "YouTube", timeInMinutes = 60, opens = 15)
                        ),  { code -> backStack.add(Destination.Lobby(code)) }, {})
                        //backStack.add(Destination.Lobby("000000"))
                    },
                    onJoinClick = { username ->
                        gameViewModel.updateUsername(username)
                        backStack.add(Destination.Join)
                    }
                )
            }

            // Join
            entry<Destination.Join> {
                JoinScreen(
                    username = username,
                    onJoinRoomSuccess = { code ->
                        gameViewModel.updateRoomCode(code)
                        backStack.add(Destination.Lobby(code))
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            // Lobby
            entry<Destination.Lobby> { destination ->
                LobbyScreen(
                    roomCode = destination.roomCode,
                    onGameStart = {
                        backStack.add(Destination.Game(destination.roomCode))
                    },
                    onBackClick = {}
                )
            }

            // Game
            entry<Destination.Game> { destination ->
                GameScreen(
                    roomCode = destination.roomCode,
                    onBackToHome = {
                        backStack.clear()
                        backStack.add(Destination.Home)
                    }
                )
            }
        }
    )
}