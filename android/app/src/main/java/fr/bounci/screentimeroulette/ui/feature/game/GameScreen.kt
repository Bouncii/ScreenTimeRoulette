package fr.bounci.screentimeroulette.ui.feature.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GameScreen(
    roomCode: String,
    onBackToHome: () -> Unit
) {
    Text(text = "Écran de Jeu - Salon : $roomCode")
}