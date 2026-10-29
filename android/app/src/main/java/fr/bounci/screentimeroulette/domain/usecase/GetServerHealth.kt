package fr.bounci.screentimeroulette.domain.usecase

import fr.bounci.screentimeroulette.domain.data.ServerHealth
import fr.bounci.screentimeroulette.repository.ServerHealthRepository

object GetServerHealth {
    suspend operator fun invoke(): ServerHealth {
        return ServerHealthRepository.getHealth()
    }
}