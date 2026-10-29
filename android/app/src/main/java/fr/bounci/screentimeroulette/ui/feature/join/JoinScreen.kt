package fr.bounci.screentimeroulette.ui.feature.join

import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun JoinScreen(
    username : String,
    onJoinRoomSuccess: (roomCode: String) -> Unit,
    onBackClick: () -> Unit,
) {
    var roomCode by remember { mutableStateOf("") }

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
                    onClick = {
                    },
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
                text = "Join",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    softWrap = false
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 56.dp)
                        .height(70.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(50.dp),
                            shadow = Shadow(
                                radius = 4.dp,
                                spread = 4.dp,
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = DpOffset(x = 0.dp, y = 4.dp)
                            )
                        )
                        .background(
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(start = 24.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    TextField(
                        value = roomCode,
                        onValueChange = { roomCode = it },
                        placeholder = {
                            Text(
                                text = "Room Id...",
                                color = MaterialTheme.colorScheme.background,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.labelSmall,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.background,
                            unfocusedTextColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(
                        onClick = {
                            if (roomCode.isNotBlank()) {
                                onJoinRoomSuccess(roomCode)
                            }
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_front),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
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
                            text = "Scan QR",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            softWrap = false
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_qr_scanner),
                            contentDescription = "Scan QR",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun JoinScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            JoinScreen(
                "Luluvroumette",
                onJoinRoomSuccess = {},
                onBackClick = {},
            )
        }
    }
}