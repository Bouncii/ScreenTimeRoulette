package fr.bounci.screentimeroulette.ui.feature.history

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

@Composable
fun GameDetailScreen(
    matchDetail: GameDetailUiModel,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(50.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Parameters",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        
        Text(
            text = stringResource(R.string.match_detail_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = matchDetail.dateString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(R.string.history_match_info_format, matchDetail.playerCount, matchDetail.roundCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(matchDetail.rounds) { round ->
                RoundDetailCard(round = round)
            }
        }
    }
}

@Composable
fun RoundDetailCard(round: RoundDetailUiModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(radius = 8.dp, color = Color.Black.copy(alpha = 0.3f), offset = DpOffset(0.dp, 4.dp))
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(R.string.match_detail_round_title, round.roundNumber, round.appName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = " " + stringResource(R.string.match_detail_app_stats, round.timeString, round.opensCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                round.playerGuesses.forEach { guess ->
                    PlayerGuessRow(guess = guess)
                }
            }

            Text(
                text = stringResource(R.string.match_detail_correct_choice, round.correctPlayerName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PlayerGuessRow(guess: PlayerGuessUiModel) {
    val isPositive = guess.pointsEarned > 0
    val pointsColor = if (isPositive) {
        Color(0xFF66BB6A)
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${guess.rank} ${guess.playerName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = " " + stringResource(R.string.match_detail_guessed, guess.guessedName),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.match_detail_points, guess.pointsEarned),
            style = MaterialTheme.typography.bodySmall,
            color = pointsColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameDetailScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

            val mockGuesses = listOf(
                PlayerGuessUiModel(1, "Synnfall", "Sipera", 20),
                PlayerGuessUiModel(2, "luluvroumette", "Sipera", 10),
                PlayerGuessUiModel(3, "Sipera", "luluvroumette", 0),
                PlayerGuessUiModel(4, "Lasnelus", "synnfall", 0)
            )

            val mockRounds = List(4) { index ->
                RoundDetailUiModel(
                    roundNumber = 1,
                    appName = "instagram",
                    timeString = "1h 45m",
                    opensCount = 28,
                    playerGuesses = mockGuesses,
                    correctPlayerName = "Sipera"
                )
            }

            val mockData = GameDetailUiModel(
                dateString = "Oct 24, 2026 - 18:32",
                playerCount = 4,
                roundCount = 5,
                rounds = mockRounds
            )

            GameDetailScreen(
                matchDetail = mockData,
                onBackClick = {},
                onSettingsClick = {}
            )
        }
    }
}