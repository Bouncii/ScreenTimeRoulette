package fr.bounci.screentimeroulette.ui.feature.game

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.data.PlayerToVote
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

@Composable
fun GuessView(
    appUsage: AppUsage?,
    candidates: List<PlayerToVote>,
    timeRemaining: Int,
    hasVoted: Boolean,
    onVote: (String) -> Unit
) {
    val buttonColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(R.string.guess_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 12.dp,
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = DpOffset(0.dp, 6.dp)
                    )
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.guess_app_label),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
            Text(
                text = appUsage?.name ?: stringResource(R.string.unknown_value),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.guess_screen_time_label),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )

            val totalMinutes = appUsage?.timeInMinutes ?: 0
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val formattedTime = if (hours > 0) {
                stringResource(R.string.time_hours_minutes, hours, minutes)
            } else {
                stringResource(R.string.time_minutes, minutes)
            }
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.guess_opened_format, appUsage?.opens ?: 0),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }

        Text(
            text = stringResource(R.string.time_seconds, timeRemaining),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(candidates) { index, candidate ->
                val backgroundColor = buttonColors[index % buttonColors.size]

                Button(
                    onClick = { onVote(candidate.id) },
                    enabled = !hasVoted,
                    modifier = Modifier
                        .height(64.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(50.dp),
                            shadow = Shadow(
                                radius = 4.dp,
                                spread = 4.dp,
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = DpOffset(x = 0.dp, y = 4.dp)
                            )
                        ),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = backgroundColor,
                        disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = candidate.pseudo,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hasVoted) Color.White.copy(alpha = 0.7f) else Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GuessViewPreview() {
    ScreenTimeRouletteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GuessView(
                appUsage = AppUsage("Screen Time Roulette", 1238, 38),
                candidates = listOf(
                    PlayerToVote("1", "Bounci"),
                    PlayerToVote("2", "Synnfall"),
                    PlayerToVote("3", "Lasnelus"),
                    PlayerToVote("4", "Sipera")
                ),
                timeRemaining = 30,
                hasVoted = false,
                onVote = {}
            )
        }
    }
}