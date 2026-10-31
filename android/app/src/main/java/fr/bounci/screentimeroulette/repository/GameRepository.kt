package fr.bounci.screentimeroulette.repository

import fr.bounci.screentimeroulette.domain.data.AppUsage
import fr.bounci.screentimeroulette.domain.data.GameEnd
import fr.bounci.screentimeroulette.domain.data.Player
import fr.bounci.screentimeroulette.domain.data.Round
import fr.bounci.screentimeroulette.domain.data.RoundEnd
import fr.bounci.screentimeroulette.remote.ApiSocket
import fr.bounci.screentimeroulette.remote.RemoteAppUsageData
import fr.bounci.screentimeroulette.remote.RemoteCreateRoomPayload
import fr.bounci.screentimeroulette.remote.RemoteGameEndData
import fr.bounci.screentimeroulette.remote.RemoteGuessPayload
import fr.bounci.screentimeroulette.remote.RemoteJoinRoomPayload
import fr.bounci.screentimeroulette.remote.RemotePlayer
import fr.bounci.screentimeroulette.remote.RemoteRoundData
import fr.bounci.screentimeroulette.remote.RemoteRoundEndData
import fr.bounci.screentimeroulette.remote.RemoteSocketResponse
import fr.bounci.screentimeroulette.remote.SocketState
import io.socket.client.Ack
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.json.JSONObject

object GameRepository {
    private val jsonParser = Json { ignoreUnknownKeys = true }
    private val players = MutableStateFlow<List<Player>>(emptyList())
    private val currentRound = MutableStateFlow<Round?>(null)
    private val roundResult = MutableStateFlow<RoundEnd?>(null)
    private val roundEnd = MutableStateFlow<List<Player>?>(null)
    private val gameEnd = MutableStateFlow<GameEnd?>(null)
    private val gameStartedEvent = MutableSharedFlow<Unit>()

    fun getSocketState(): StateFlow<SocketState> = ApiSocket.getSocketState()

    fun getPlayers(): StateFlow<List<Player>> = players.asStateFlow()
    fun getCurrentRound(): StateFlow<Round?> = currentRound.asStateFlow()
    fun getRoundResult(): StateFlow<RoundEnd?> = roundResult.asStateFlow()
    fun getRoundEnd(): StateFlow<List<Player>?> = roundEnd.asStateFlow()
    fun getGameEnd(): StateFlow<GameEnd?> = gameEnd.asStateFlow()

    fun getGameStartedEvent(): SharedFlow<Unit> = gameStartedEvent.asSharedFlow()

    fun registerListeners() {
        val socket = ApiSocket.getSocket()!!

        socket.off("playerJoined")
        socket.off("gameStarted")
        socket.off("startRound")
        socket.off("roundEnded")
        socket.off("gameEnded")

        socket.on("playerJoined") { args ->
            val jsonString = args.firstOrNull()?.toString() ?: return@on
            try {
                val remotePlayers = jsonParser.decodeFromString<List<RemotePlayer>>(jsonString)
                players.value = remotePlayers.map { it.parse() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        socket.on("gameStarted") {
            gameStartedEvent.tryEmit(Unit)
        }

        socket.on("startRound") { args ->
            val jsonString = args.firstOrNull()?.toString() ?: return@on
            try {
                val remoteData = jsonParser.decodeFromString<RemoteRoundData>(jsonString)
                roundResult.value = null
                currentRound.value = remoteData.parse()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        socket.on("roundEnded") { args ->
            val jsonString = args.firstOrNull()?.toString() ?: return@on
            try {
                val remoteData = jsonParser.decodeFromString<RemoteRoundEndData>(jsonString)
                roundResult.value = remoteData.parse()
                players.value = remoteData.leaderboard.map { it.parse() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        socket.on("gameEnded") { args ->
            val jsonString = args.firstOrNull()?.toString() ?: return@on
            try {
                val remoteData = jsonParser.decodeFromString<RemoteGameEndData>(jsonString)
                gameEnd.value = remoteData.parse()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createRoom(
        pseudo: String,
        screenData: List<AppUsage>,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val socket = ApiSocket.getSocket()!!

        val remoteScreenData = screenData.map {
            RemoteAppUsageData(it.name, it.timeInMinutes, it.opens)
        }

        val payload = RemoteCreateRoomPayload(pseudo, remoteScreenData)
        val jsonString = jsonParser.encodeToString(payload)

        socket.emit("createRoom", JSONObject(jsonString), Ack { args ->
            val responseStr = args.firstOrNull()?.toString() ?: return@Ack onError()
            val response = jsonParser.decodeFromString<RemoteSocketResponse>(responseStr)
            if (response.success && response.roomCode != null) {
                onSuccess(response.roomCode)
            } else {
                onError()
            }
        })
    }

    fun joinRoom(
        roomCode: String,
        pseudo: String,
        screenData: List<AppUsage>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val socket = ApiSocket.getSocket()!!

        val remoteScreenData = screenData.map {
            RemoteAppUsageData(it.name, it.timeInMinutes, it.opens)
        }

        val payload = RemoteJoinRoomPayload(roomCode, pseudo, remoteScreenData)
        val jsonString = jsonParser.encodeToString(payload)

        socket.emit("joinRoom", JSONObject(jsonString), Ack { args ->
            val responseStr = args.firstOrNull()?.toString() ?: return@Ack onError()
            val response = jsonParser.decodeFromString<RemoteSocketResponse>(responseStr)
            if (response.success) {
                onSuccess()
            } else {
                onError()
            }

        })
    }

    fun startGame(onSuccess: () -> Unit, onError: () -> Unit) {
        val socket = ApiSocket.getSocket()!!

        socket.emit("startGame", JSONObject(), Ack { args ->
            val responseStr = args.firstOrNull()?.toString() ?: return@Ack onError()
            val response = jsonParser.decodeFromString<RemoteSocketResponse>(responseStr)
            if (response.success) {
                onSuccess()
            } else {
                onError()
            }
        })
    }

    fun submitGuess(guessedPlayerId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val socket = ApiSocket.getSocket()!!

        val payload = RemoteGuessPayload(guessedPlayerId)
        val jsonString = jsonParser.encodeToString(payload)

        socket.emit("submitGuess", JSONObject(jsonString), Ack { args ->
            val responseStr = args.firstOrNull()?.toString() ?: return@Ack onError()
            try {
                val response = jsonParser.decodeFromString<RemoteSocketResponse>(responseStr)
                if (response.success) {
                    onSuccess()
                } else {
                    onError()
                }
            } catch (e: Exception) {
                onError()
            }
        })
    }
}