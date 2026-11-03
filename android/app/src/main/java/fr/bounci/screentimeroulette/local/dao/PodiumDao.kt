package fr.bounci.screentimeroulette.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import fr.bounci.screentimeroulette.local.entity.PodiumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PodiumDao {
    @Upsert
    suspend fun upsert(podium: PodiumEntity)

    @Query(value = "SELECT * FROM Podium ORDER BY timeString DESC")
    fun getPodiums(): Flow<List<PodiumEntity>>
}