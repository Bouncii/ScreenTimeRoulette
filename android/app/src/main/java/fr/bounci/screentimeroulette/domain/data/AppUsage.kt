package fr.bounci.screentimeroulette.domain.data

//Stats sur les app genre Instagram, 12h, 34 opens
data class AppUsage(
    val name: String,
    val timeInMinutes: Int,
    val opens: Int
)
