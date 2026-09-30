package fr.bounci.screentimeroulette.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.bounci.screentimeroulette.ui.feature.game.GameScreen
import fr.bounci.screentimeroulette.ui.feature.home.HomeScreen
import fr.bounci.screentimeroulette.ui.feature.join.JoinScreen
import fr.bounci.screentimeroulette.ui.feature.lobby.LobbyScreen

@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(Destination.Home)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            // Home
            entry<Destination.Home> {
                HomeScreen(
                    onCreateClick = {
                        backStack.add(Destination.Lobby("ABCD"))
                    },
                    onJoinClick = {
                        backStack.add(Destination.Join)
                    }
                )
            }

            // Join
            entry<Destination.Join> {
                JoinScreen(
                    onJoinRoomSuccess = { code ->
                        backStack.add(Destination.Lobby(code))
                    }
                )
            }

            // Lobby
            entry<Destination.Lobby> { destination ->
                LobbyScreen(
                    roomCode = destination.roomCode,
                    onGameStart = {
                        backStack.add(Destination.Game(destination.roomCode))
                    }
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