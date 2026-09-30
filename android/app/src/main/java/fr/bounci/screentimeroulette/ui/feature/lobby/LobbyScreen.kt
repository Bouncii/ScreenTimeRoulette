package fr.bounci.screentimeroulette.ui.feature.lobby

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LobbyScreen(
    roomCode: String,
    onGameStart: () -> Unit
) {
    Text(text = "Lobby du salon : $roomCode")
}