package fr.bounci.screentimeroulette.ui.session

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _roomCode = MutableStateFlow("")
    val roomCode = _roomCode.asStateFlow()

    fun updateUsername(name: String) {
        _username.value = name
    }

    fun updateRoomCode(code: String) {
        _roomCode.value = code
    }
}