package fr.bounci.screentimeroulette.ui.feature.history

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

data class GameHistoryUiModel(
    val id: String,
    val dateString: String,
    val winnerName: String,
    val playerCount: Int,
    val roundCount: Int
)

@Composable
fun HistoryScreen(
    historyList: List<GameHistoryUiModel>,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMatchClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
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
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(historyList, key = { it.id }) { item ->
                HistoryCard(
                    item = item,
                    onClick = { onMatchClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun HistoryCard(
    item: GameHistoryUiModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(radius = 8.dp, color = Color.Black.copy(alpha = 0.3f), offset = DpOffset(0.dp, 4.dp))
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = item.dateString,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_trophy),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.history_winner_format, item.winnerName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.history_match_info_format, item.playerCount, item.roundCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Text(
                    text = stringResource(R.string.history_see_details),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val mockData = listOf(
                GameHistoryUiModel("1", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5),
                GameHistoryUiModel("2", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5),
                GameHistoryUiModel("3", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5),
                GameHistoryUiModel("4", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5),
                GameHistoryUiModel("5", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5),
                GameHistoryUiModel("6", "Oct 24, 2026 - 18:32", "Synnfall", 4, 5)
            )

            HistoryScreen(
                historyList = mockData,
                onBackClick = {},
                onSettingsClick = {},
                onMatchClick = {}
            )
        }
    }
}