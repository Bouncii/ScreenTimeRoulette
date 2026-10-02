package fr.bounci.screentimeroulette.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary400,
    onPrimary = Background900,
    secondary = Secondary400,
    onSecondary = Secondary900,
    tertiary = Accent600,
    error = Danger600,
    onError = Color.White,
    background = Background500,
    onBackground = Color.White,
    surface = Background800,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    onPrimary = Color.White,
    secondary = Secondary500,
    onSecondary = Background900,
    tertiary = Accent500,
    error = Danger500,
    onError = Color.White,
    background = Background100,
    onBackground = Background900,
    surface = Color.White,
    onSurface = Background900
)

@Composable
fun ScreenTimeRouletteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}