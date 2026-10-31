package fr.bounci.screentimeroulette.domain.usecase

import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.repository.GameRepository

object CreateRoom {
    operator fun invoke(
        pseudo: String,
        screenData: List<AppUsage>,
        onSuccess: (String) -> Unit, // Pour obtenir le code de la room on l'envoie via cette fct
        onError: () -> Unit
    ) {
        GameRepository.registerListeners()
        GameRepository.createRoom(pseudo,screenData,onSuccess,onError)
    }
}