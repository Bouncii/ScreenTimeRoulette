package fr.bounci.screentimeroulette.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.bounci.screentimeroulette.domain.data.GameEnd
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.domain.data.PodiumData
import kotlinx.serialization.json.Json

@Entity(tableName = "Podium")
data class PodiumEntity(
    @PrimaryKey val timeString: Long,
    val roomId: String,
    val totalRounds: Int,
    val podium: String
) {
    fun toPodiumData(): PodiumData {
        return PodiumData(
            timeString = timeString,
            gameEnd = GameEnd(
                roomId = roomId,
                totalRounds = totalRounds,
                podium = Json.decodeFromString<List<Player>>(podium)
            )
        )
    }

    companion object {
        fun fromPodiumData(data: PodiumData): PodiumEntity {
            return PodiumEntity(
                timeString = data.timeString,
                roomId = data.gameEnd.roomId,
                totalRounds = data.gameEnd.totalRounds,
                podium = Json.encodeToString(data.gameEnd.podium)
            )
        }
    }
}
