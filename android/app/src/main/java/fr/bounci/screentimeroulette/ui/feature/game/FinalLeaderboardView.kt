package fr.bounci.screentimeroulette.ui.feature.game

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

@Composable
fun FinalLeaderboardView(
    podium: List<Player>,
    onHomeClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {
    val winner = podium.getOrNull(0)
    val second = podium.getOrNull(1)
    val third = podium.getOrNull(2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(radius = 8.dp, color = Color.Black.copy(alpha = 0.3f), offset = DpOffset(0.dp, 4.dp))
                )
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Winner",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = winner?.pseudo ?: "???",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_trophy),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(56.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                PodiumBlock(
                    pseudo = second?.pseudo ?: "",
                    rank = 2,
                    height = 90.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                PodiumBlock(
                    pseudo = "",
                    rank = 1,
                    height = 130.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )

                PodiumBlock(
                    pseudo = third?.pseudo ?: "",
                    rank = 3,
                    height = 75.dp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(250.dp)
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(radius = 8.dp, color = Color.Black.copy(alpha = 0.3f), offset = DpOffset(0.dp, 4.dp))
                )
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(podium) { index, player ->
                    CompactLeaderboardItem(rank = index + 1, player = player)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onHomeClick,
                modifier = Modifier
                    .weight(1f)
                    .height(75.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Home", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground, softWrap = false)
            }

            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier
                    .weight(1f)
                    .height(75.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Play Again", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground, softWrap = false)
            }
        }
    }
}

@Composable
fun PodiumBlock(
    pseudo: String,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (pseudo.isNotEmpty()) {
            Text(
                text = pseudo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    color = color,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
            )
        }
    }
}

@Composable
fun CompactLeaderboardItem(rank: Int, player: Player) {
    val rankBackgroundColor = when (rank) {
        1 -> Color(0xFFF4D068)
        2 -> Color(0xFF9CB4B8)
        3 -> Color(0xFFE08D3C)
        else -> Color.Transparent
    }

    val rankTextColor = when (rank) {
        1, 2, 3 -> Color(0xFF1C2A33)
        else -> Color.White
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(color = rankBackgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = rankTextColor,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = player.pseudo,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${player.score} pts",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FinalLeaderboardViewPreview() {
    ScreenTimeRouletteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FinalLeaderboardView(
                podium = listOf(
                    Player("1", "Synnfall", 120),
                    Player("2", "luluvroumette", 100),
                    Player("3", "Sipera", 80),
                    Player("4", "Lasnelus", 50),
                    Player("5", "Tartempion", 40),
                    Player("6", "JoueurMystere", 20)
                ),
                onHomeClick = {},
                onPlayAgainClick = {}
            )
        }
    }
}