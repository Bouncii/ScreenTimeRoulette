package fr.bounci.screentimeroulette.ui.feature.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Text(text = "Écran Home")
}