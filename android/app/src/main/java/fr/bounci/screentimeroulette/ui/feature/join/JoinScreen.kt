package fr.bounci.screentimeroulette.ui.feature.join

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun JoinScreen(
    onJoinRoomSuccess: (roomCode: String) -> Unit
) {
    Text(text = "Écran Rejoindre")
}