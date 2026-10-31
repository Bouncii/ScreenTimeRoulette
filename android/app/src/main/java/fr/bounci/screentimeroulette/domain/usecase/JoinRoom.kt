package fr.bounci.screentimeroulette.domain.usecase

import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.repository.GameRepository

object JoinRoom {
    operator fun invoke(
        roomCode: String,
        pseudo: String,
        screenData: List<AppUsage>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        GameRepository.registerListeners()
        GameRepository.joinRoom(roomCode,pseudo,screenData,onSuccess,onError)
    }
}