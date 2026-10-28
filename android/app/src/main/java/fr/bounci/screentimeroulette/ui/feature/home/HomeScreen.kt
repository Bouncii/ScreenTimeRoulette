package fr.bounci.screentimeroulette.ui.feature.home
import android.content.res.Configuration
import fr.bounci.screentimeroulette.R

import androidx.compose.foundation.Image
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
import fr.bounci.screentimeroulette.ui.theme.ScreenTimeRouletteTheme

@Composable
fun HomeScreen(
    onCreateClick: (String) -> Unit,
    onJoinClick: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize() ,
        ) {
            // Modifier png si ajout de theme light
            Image(
                painter = painterResource(id = R.drawable.logos_gradient),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
            ){
                Column(
                    verticalArrangement = Arrangement.spacedBy(50.dp),
                ){
                    Text(
                        text = "Screen Time Roulette",
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 56.dp)
                                .height(70.dp)
                                .dropShadow(
                                    shape = RoundedCornerShape(50.dp),
                                    shadow = Shadow(
                                        radius = 4.dp,
                                        spread = 0.dp,
                                        color = Color.Black.copy(alpha = 0.25f),
                                        offset = DpOffset(x = 0.dp, y = 4.dp)
                                    )
                                ),
                            placeholder = {
                                Text(
                                    text = "Username...",
                                    color = MaterialTheme.colorScheme.background,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            textStyle = MaterialTheme.typography.labelMedium,
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                                focusedTextColor = MaterialTheme.colorScheme.background,
                                unfocusedTextColor = MaterialTheme.colorScheme.background,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {onCreateClick(username)},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(70.dp)
                                    .dropShadow(
                                        shape = RoundedCornerShape(50.dp),
                                        shadow = Shadow(
                                            radius = 4.dp,
                                            color = Color.Black.copy(alpha = 0.25f),
                                            offset = DpOffset(0.dp, 4.dp)
                                        )
                                    ),
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text(
                                    text = "+ Create",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    softWrap = false
                                )
                            }

                            Button(
                                onClick = {onJoinClick(username)},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(70.dp)
                                    .dropShadow(
                                        shape = RoundedCornerShape(50.dp),
                                        shadow = Shadow(
                                            radius = 4.dp,
                                            color = Color.Black.copy(alpha = 0.25f),
                                            offset = DpOffset(0.dp, 4.dp)
                                        )
                                    ),
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "+ Join",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    IconButton(
                        onClick = {
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Parameters",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(70.dp)
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
fun HomeScreenPreview() {
    ScreenTimeRouletteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                onCreateClick = {},
                onJoinClick = {}
            )
        }
    }
}