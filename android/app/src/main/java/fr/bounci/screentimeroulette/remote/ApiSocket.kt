package fr.bounci.screentimeroulette.remote

import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ApiSocket {
    private var socket: Socket? = null
    private const val BASE_URL = "https://str.cmi-info.fr/"

    private val socketState = MutableStateFlow<SocketState>(SocketState.DISCONNECTED)

    fun getSocketState(): StateFlow<SocketState> = socketState.asStateFlow()

    fun connect() {
        if (socket != null && socket?.connected() == true) return

        socketState.value = SocketState.CONNECTING;

        val options = IO.Options().apply {
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE // Le socket est central il faut qu'il ce relance tout le temps
            reconnectionDelay = 1000
        }
        socket = IO.socket(BASE_URL, options)

        socket?.on(Socket.EVENT_CONNECT) {
            socketState.value = SocketState.CONNECTED
        }
        socket?.on(Socket.EVENT_DISCONNECT) { args ->
            socketState.value = SocketState.DISCONNECTED
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            socketState.value = SocketState.ERROR
        }

        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
        socketState.value = SocketState.DISCONNECTED
    }

    fun getSocket(): Socket? = socket
}