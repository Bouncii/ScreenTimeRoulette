package fr.bounci.screentimeroulette.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.bounci.screentimeroulette.R

val Fredoka = FontFamily(
    Font(R.font.fredoka_regular, FontWeight.Normal),
    Font(R.font.fredoka_medium, FontWeight.Medium),
    Font(R.font.fredoka_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(

    displayLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 55.sp, lineHeight = 55.sp),
    displayMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 36.sp, lineHeight = 44.sp),


    headlineLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 70.sp, lineHeight = 55.sp),
    headlineMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 50.sp, lineHeight = 40.sp),
    headlineSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 32.sp),


    titleLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 40.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 40.sp, lineHeight = 30.sp),
    titleSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 60.sp, lineHeight = 30.sp),


    bodyLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),


    labelLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Medium, fontSize = 30.sp, lineHeight = 25.sp),
    labelSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp)
)