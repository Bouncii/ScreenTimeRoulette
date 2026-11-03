package fr.bounci.screentimeroulette.repository

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import fr.bounci.screentimeroulette.domain.data.AppUsage
import java.util.Calendar

data class TempPackageStats(var milliseconds: Long, var openCount: Int)

class AppUsageRepository(private val context: Context) {

    fun get5AppsWithMostTimeSpend(): List<AppUsage> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = context.packageManager

        // on prend l'heure actuel
        val c = Calendar.getInstance()
        val endTime = c.timeInMillis // Maintenant

        // on prend l'heure d'il y a 7 jours
        c.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = c.timeInMillis

        // On collecte des raw statistiques
        val packagesStat = collectUsageStats(usageStatsManager, startTime, endTime)

        // On filtre et on formate avec les vrais noms
        val validApps = filterAndFormatApps(packagesStat, packageManager)

        return validApps
            .sortedByDescending { it.timeInMinutes }
            .take(5)
    }

    private fun collectUsageStats(
        usageStatsManager: UsageStatsManager,
        startTime: Long,
        endTime: Long
    ): Map<String, TempPackageStats> {

        val packagesStat = mutableMapOf<String, TempPackageStats>()

        // On demande à Android ses temps
        val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        for ((packageName, stats) in aggregatedStats) {
            // On récupère le temps total
            packagesStat[packageName] = TempPackageStats(
                milliseconds = stats.totalTimeInForeground,
                openCount = 0 // On contera le nombre d'ouverture après
            )
        }

        // On prend toutes les ouvertures et fermeture de toutes les app
        // Incompréhensible a première vue mais faut se dire que events
        // est un fichier/input/curseur et currentEvent un buffer donc tant qu'il y a
        // des input d'event non lus, on continue de vider le fichier dans le buffer
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val currentEvent = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(currentEvent)
            val packageName = currentEvent.packageName

            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                packagesStat[packageName]?.apply { openCount++ }
            }
        }

        return packagesStat
    }

    private fun filterAndFormatApps(
        packagesStat: Map<String, TempPackageStats>,
        packageManager: PackageManager
    ): List<AppUsage> {

        // On prépare une liste vide classique pour stocker nos résultats
        val finalAppsList = mutableListOf<AppUsage>()

        // On parcourt notre dictionnaire stats par stats
        for ((packageName, tempAppData) in packagesStat) {

            // miliseconde vers minutes
            val timeInMinutes = (tempAppData.milliseconds / 60000).toInt()

            // Pour filtrer plein de truc bizarre on vérif que l'app a une icone
            if (packageManager.getLaunchIntentForPackage(packageName) == null) {
                continue
            }

            try {
                // On récup le vrai nom de l'app
                val appMetaData = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val realAppName = packageManager.getApplicationLabel(appMetaData).toString()

                val appUsage = AppUsage(
                    name = realAppName,
                    timeInMinutes = timeInMinutes,
                    opens = tempAppData.openCount
                )
                finalAppsList.add(appUsage)

            } catch (_: Exception) {
                continue
            }
        }

        return finalAppsList
    }
}