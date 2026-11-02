package fr.bounci.screentimeroulette.repository

import android.content.Context
import fr.bounci.screentimeroulette.local.UserData

class UserRepository(context: Context) {

    private val prefs = context.getSharedPreferences("ScreenTimePrefs", Context.MODE_PRIVATE)
    private val localData = UserData(prefs)
    fun saveUsername(pseudo: String) {
        localData.saveUsername(pseudo)
    }

    fun getUsername(): String {
        return localData.getUsername() ?: ""
    }
}