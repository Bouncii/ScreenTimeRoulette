package fr.bounci.screentimeroulette.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.bounci.screentimeroulette.local.dao.PodiumDao
import fr.bounci.screentimeroulette.local.entity.PodiumEntity

@Database(
    entities = [PodiumEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PodiumDataBase : RoomDatabase() {
    abstract fun podiumDao(): PodiumDao

    companion object {
        private const val DB_NAME = "podium_database"

        @Volatile
        private var INSTANCE: PodiumDataBase? = null

        fun get(context: Context): PodiumDataBase {
            if (INSTANCE == null) {
                synchronized(this) {
                    Room.databaseBuilder(context, PodiumDataBase::class.java, DB_NAME).build()
                        .also { INSTANCE = it }
                }
            }
            return INSTANCE!!
        }
    }
}

data object PodiumDatabaseHolder {
    var database: PodiumDataBase? = null

    fun initialize(context: Context) {
        database = PodiumDataBase.get(context)
    }
}