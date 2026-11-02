package fr.bounci.screentimeroulette.local

import android.content.SharedPreferences
import androidx.core.content.edit

class UserData(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val KEY_USERNAME = "USERNAME"
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit(commit = true) { putString(KEY_USERNAME, username) }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
}