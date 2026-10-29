package fr.bounci.screentimeroulette.remote

import fr.bounci.screentimeroulette.domain.data.ServerHealth
import kotlinx.serialization.Serializable

@Serializable
data class RemoteHealthResponse(
    val status: String,
    val activeRooms: Int,
    val uptime : Double,
) {
    fun toApiHealth(): ServerHealth {
        return ServerHealth(
            status,
            activeRooms,
            uptime
        )
    }
}
