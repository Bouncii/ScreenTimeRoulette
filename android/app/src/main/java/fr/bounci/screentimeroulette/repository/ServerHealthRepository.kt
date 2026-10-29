package fr.bounci.screentimeroulette.repository

import fr.bounci.screentimeroulette.domain.data.ServerHealth
import fr.bounci.screentimeroulette.remote.HealthApi

object ServerHealthRepository {
    suspend fun getHealth() : ServerHealth{
        val response = HealthApi.fetchApiHealth()
        return response.toApiHealth()
    }
}