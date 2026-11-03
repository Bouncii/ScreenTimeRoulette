package fr.bounci.screentimeroulette.ui.feature.history

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.domain.data.GameEnd
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.domain.data.PodiumData
import fr.bounci.screentimeroulette.ui.feature.game.CompactLeaderboardItem
import fr.bounci.screentimeroulette.ui.feature.game.PodiumBlock
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameDetailScreen(
    matchDetail: PodiumData,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val podiumList = matchDetail.gameEnd.podium
    val winner = podiumList.getOrNull(0)
    val second = podiumList.getOrNull(1)
    val third = podiumList.getOrNull(2)

    val dateString = remember(matchDetail.timeString) {
        SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date(matchDetail.timeString))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                text = dateString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(
                    R.string.history_match_info_format,
                    podiumList.size,
                    matchDetail.gameEnd.totalRounds
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        )

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
                text = stringResource(R.string.final_winner_label),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = winner?.pseudo ?: stringResource(R.string.unknown_value),
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
                itemsIndexed(podiumList) { index, player ->
                    CompactLeaderboardItem(rank = index + 1, player = player)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameDetailScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val mockData = PodiumData(
                timeString = System.currentTimeMillis(),
                gameEnd = GameEnd(
                    roomId = "X7B9Q2",
                    totalRounds = 5,
                    podium = listOf(
                        Player("1", "Sipera", 2840),
                        Player("2", "Luluvroumette", 1950),
                        Player("3", "Synnfall", 910),
                        Player("4", "Lasnelus", 0)
                    )
                )
            )

            GameDetailScreen(
                matchDetail = mockData,
                onBackClick = {},
                onSettingsClick = {}
            )
        }
    }
}