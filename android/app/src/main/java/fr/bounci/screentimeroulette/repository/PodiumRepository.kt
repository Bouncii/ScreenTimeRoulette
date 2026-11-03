package fr.bounci.screentimeroulette.repository

import fr.bounci.screentimeroulette.domain.data.PodiumData
import fr.bounci.screentimeroulette.local.PodiumDatabaseHolder
import fr.bounci.screentimeroulette.local.entity.PodiumEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

object PodiumRepository {
    suspend fun savePodium(podium: PodiumData) {
        PodiumDatabaseHolder.database?.podiumDao()?.upsert(PodiumEntity.fromPodiumData(podium))
    }

    fun getPodiums(): Flow<List<PodiumData>> {
        return PodiumDatabaseHolder.database?.podiumDao()
            ?.getPodiums()
            ?.map { entities ->
            entities.map { it.toPodiumData() }
            }
            ?: flowOf(emptyList())
    }
}