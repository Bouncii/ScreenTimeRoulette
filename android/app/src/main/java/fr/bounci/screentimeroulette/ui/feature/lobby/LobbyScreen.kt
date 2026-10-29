package fr.bounci.screentimeroulette.ui.feature.lobby

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import fr.bounci.screentimeroulette.R
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

@Composable
fun LobbyScreen(
    roomCode: String,
    players: List<String> = emptyList(),
    isHost: Boolean = true,
    onGameStart: () -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(60.dp)
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

            Column(

                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(
                    text = "Game PIN",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(horizontal = 20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(start = 20.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = roomCode,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_copy),
                            contentDescription = "Copy PIN",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(50.dp),
                            shadow = Shadow(
                                radius = 4.dp,
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = DpOffset(4.dp, 4.dp)
                            )
                        )
                    ,

                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Show QR",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            softWrap = false
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_qr_code),
                            contentDescription = "Show QR",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Players",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(players) { player ->
                        Text(
                            text = player,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            if (isHost) {
                Button(
                    onClick = onGameStart,
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                        .height(90.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(35.dp),
                            shadow = Shadow(
                                radius = 4.dp,
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = DpOffset(4.dp, 4.dp)
                            )
                        ),

                    shape = RoundedCornerShape(35.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Launch",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            softWrap = false
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_front),
                            contentDescription = "Show QR",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }else{
                Text(
                    text = "Waiting for host to start...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                        .height(90.dp),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Lobby Host View"
)
@Composable
fun LobbyScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LobbyScreen(
                roomCode = "584325",
                players = listOf("Luluvroumette", "Th3_Warrior", "Synnfall", "Lasnelus", "Sipera"),
                isHost = false,
                onGameStart = {},
                onBackClick = {}
            )
        }
    }
}