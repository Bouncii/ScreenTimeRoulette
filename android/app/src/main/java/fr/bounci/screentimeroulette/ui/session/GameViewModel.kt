package fr.bounci.screentimeroulette.ui.session

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.usecase.CreateRoom
import fr.bounci.screentimeroulette.domain.usecase.JoinRoom
import fr.bounci.screentimeroulette.repository.AppUsageRepository
import fr.bounci.screentimeroulette.repository.GameRepository
import fr.bounci.screentimeroulette.repository.UserRepository
import fr.bounci.screentimeroulette.ui.feature.game.GamePhase
import fr.bounci.screentimeroulette.ui.feature.game.GameUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    private val appUsageRepo = AppUsageRepository(application)
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val gameStartedEvent: SharedFlow<Unit> = GameRepository.getGameStartedEvent()

    private var timerJob: Job? = null

    init {
        val savedName = userRepository.getUsername()
        if (savedName.isNotBlank()) {
            _uiState.update { it.copy(username = savedName) }
        }
        GameRepository.registerListeners()
        observeRepository()
    }
    fun saveCurrentUsername() {
        userRepository.saveUsername(_uiState.value.username)
    }
    fun updateUsername(name: String) {
        _uiState.update { it.copy(username = name) }
    }

    fun createRoom(onSuccess: (String) -> Unit) {
        val currentName = _uiState.value.username
        CreateRoom(
            pseudo = currentName,
            screenData = appUsageRepo.get5AppsWithMostTimeSpend(),
            onSuccess = { code ->
                _uiState.update { it.copy(roomCode = code, isHost = true) }
                onSuccess(code)
            },
            onError = {}
        )
    }

    fun joinRoom(code: String, onSuccess: () -> Unit) {
        val currentName = _uiState.value.username
        JoinRoom(
            roomCode = code,
            pseudo = currentName,
            screenData = appUsageRepo.get5AppsWithMostTimeSpend(),
            onSuccess = {
                _uiState.update { it.copy(roomCode = code, isHost = false) }
                onSuccess()
            },
            onError = {}
        )
    }

    fun submitGuess(candidateId: String) {
        if (_uiState.value.hasVoted) return

        GameRepository.submitGuess(
            guessedPlayerId = candidateId,
            onSuccess = { _uiState.update { it.copy(hasVoted = true) } },
            onError = {}
        )
    }

    fun launchGame() {
        GameRepository.startGame(
            onSuccess = {},
            onError = {}
        )
    }

    private fun startTimer(duration: Int) {
        timerJob?.cancel()
        _uiState.update { it.copy(timeRemaining = duration) }

        timerJob = viewModelScope.launch {
            var timeLeft = duration
            while (timeLeft > 0) {
                delay(1000.milliseconds)
                timeLeft -= 1
                _uiState.update{it.copy(timeRemaining = timeLeft)}
            }
        }
    }

    private fun observeRepository() {
        viewModelScope.launch {
            GameRepository.getPlayers().collect { newPlayers ->
                _uiState.update { it.copy(players = newPlayers) }
            }
        }


        viewModelScope.launch {
            GameRepository.getCurrentRound().collect { round ->
                if (round != null) {
                    _uiState.update {
                        it.copy(
                            currentPhase = GamePhase.GUESSING,
                            roundNumber = round.roundNumber,
                            totalRounds = round.totalRounds,
                            mysteryApp = round.screenData.firstOrNull(),
                            candidates = round.candidates,
                            timeRemaining = round.duration,
                            hasVoted = false
                        )
                    }
                    startTimer(round.duration)
                }
            }
        }


        viewModelScope.launch {
            GameRepository.getRoundResult().collect { result ->
                if (result != null) {
                    timerJob?.cancel()
                    _uiState.update {
                        it.copy(
                            currentPhase = GamePhase.ROUND_LEADERBOARD,
                            lastRoundResult = result
                        )
                    }
                    startTimer(8)
                }
            }
        }

        viewModelScope.launch {
            GameRepository.getGameEnd().collect { gameEnd ->
                if (gameEnd != null) {
                    _uiState.update {
                        it.copy(
                            currentPhase = GamePhase.FINAL_PODIUM,
                            finalGameResult = gameEnd
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { currentState ->
            GameUiState(username = currentState.username)
        }
    }
}