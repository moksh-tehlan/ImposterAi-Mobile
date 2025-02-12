package com.moksh.imposterai.data.websocket

import android.util.Log
import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.entity.ConnectionStatus
import com.moksh.imposterai.data.entity.UserEntity
import com.moksh.imposterai.data.entity.WsMessage
import com.moksh.imposterai.data.entity.request.SocketEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

class WebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @Named("socketUrl") private val socketUrl: String
) {
    private var webSocket: WebSocket? = null
    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>(
        replay = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val connectionStatus = _connectionStatus.asSharedFlow()

    private val _eventFlow = MutableSharedFlow<SocketEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun connect() {
        val request = Request.Builder()
            .url(socketUrl)
            .build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    _connectionStatus.emit(ConnectionStatus.DISCONNECTED)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    _connectionStatus.emit(ConnectionStatus.DISCONNECTING)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                CoroutineScope(Dispatchers.IO).launch {
                    _connectionStatus.emit(ConnectionStatus.ERROR)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("text", text)
                    val wsMessage = JsonConverter.fromJson<WsMessage<Any>>(text)
                    Log.d("wsMessage", wsMessage.toString())
                    when (wsMessage.action) {
                        SocketEvents.MATCH_FOUND -> {
                            wsMessage.data?.let { data ->
                                val matchData =
                                    JsonConverter.fromJson<SocketEvent.MatchFoundResponse>(
                                        data.toString()
                                    )
                                Log.d("matchData", matchData.toString())
                                _eventFlow.emit(matchData)
                            }
                        }

                        SocketEvents.CHAT -> {
                            wsMessage.data?.let { data ->
                                try {
                                    Log.d("Chat data", data.toString())
                                    val matchData = JsonConverter.fromJson<SocketEvent.Chat>(
                                        JsonConverter.toJson(data)
                                    )
                                    _eventFlow.emit(matchData)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        SocketEvents.TIMER -> {
                            wsMessage.data?.let { data ->
                                val matchData =
                                    JsonConverter.fromJson<SocketEvent.TimeLeft>(
                                        data.toString()
                                    )
                                _eventFlow.emit(matchData)
                            }
                        }

                        SocketEvents.TURN_CHANGE -> {
                            wsMessage.data?.let { data ->
                                val matchData =
                                    JsonConverter.fromJson<SocketEvent.TurnChange>(
                                        data.toString()
                                    )
                                _eventFlow.emit(matchData)
                            }
                        }

                        SocketEvents.GAME_OVER -> {
                            _eventFlow.emit(SocketEvent.GameOver)
                        }

                        else -> {}
                    }
                }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                CoroutineScope(Dispatchers.IO).launch {
                    _connectionStatus.emit(ConnectionStatus.CONNECTED)
                }
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye")
        webSocket = null
    }

    fun onSendMessage(message: String) {
        webSocket?.send(message)
    }
}

sealed interface SocketEvent {
    data class MatchFoundResponse(
        var matchId: String,
        var currentTyperId: String,
        var opponent: UserEntity,
    ) : SocketEvent

    data class TimeLeft(
        var timeLeft: Int,
    ) : SocketEvent

    data class Chat(
        var id: String,
        var sender: UserEntity,
        var message: String,
        var currentTyperId: String,
    ) : SocketEvent

    data class TurnChange(
        var userId: String,
    ) : SocketEvent

    data object GameOver : SocketEvent

}