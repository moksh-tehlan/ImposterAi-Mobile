package com.moksh.imposterai.data.websocket

import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.entity.IncomingMessage
import com.moksh.imposterai.data.entity.OutgoingMessage
import com.moksh.imposterai.data.entity.request.Chat
import com.moksh.imposterai.data.entity.request.SocketActions
import com.moksh.imposterai.data.entity.request.SocketEvents
import com.moksh.imposterai.data.entity.response.SocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

class WebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val jsonConverter: JsonConverter,
    @Named("socketUrl") private val socketUrl: String
) {
    private var webSocket: WebSocket? = null

    private val _eventFlow = MutableSharedFlow<SocketEvent>(
        replay = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventFlow = _eventFlow.asSharedFlow()

    fun connect() {
        val request = Request.Builder()
            .url(socketUrl)
            .build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    handleMessage(text)
                }
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye")
        webSocket = null
    }

    private suspend fun handleMessage(text: String) {
        val wsMessage = jsonConverter.fromJson<IncomingMessage<Any>>(text)

        val event: SocketEvent = when (wsMessage.action) {
            SocketEvents.MATCH_FOUND -> handleMatchFound(wsMessage.data)
            SocketEvents.CHAT -> handleIncomingChat(wsMessage.data)
            SocketEvents.TIMER -> handleTimer(wsMessage.data)
            SocketEvents.GAME_OVER -> handleGameOver()
            SocketEvents.PLAYER_LEFT -> handlePlayerLeft()
        }

        _eventFlow.emit(event)
    }

    private fun handleMatchFound(data: Any?): SocketEvent {
        if (data == null) return SocketEvent.ConnectionEvent.Error(error = "data is required")
        val matchFoundData =
            jsonConverter.fromJson<SocketEvent.GameState.MatchFound>(jsonConverter.toJson(data))
        return matchFoundData
    }

    private fun handleIncomingChat(data: Any?): SocketEvent {
        if (data == null) return SocketEvent.ConnectionEvent.Error(error = "data is required")
        val messageReceived =
            jsonConverter.fromJson<SocketEvent.ChatEvent.MessageReceived>(jsonConverter.toJson(data))
        return messageReceived
    }

    private fun handleTimer(data: Any?): SocketEvent {
        if (data == null) return SocketEvent.ConnectionEvent.Error(error = "data is required")
        val timeUpdate =
            jsonConverter.fromJson<SocketEvent.GameState.TimeUpdate>(jsonConverter.toJson(data))
        return timeUpdate
    }

    private fun handleGameOver(): SocketEvent {
        return SocketEvent.GameLifecycle.GameOver
    }

    private fun handlePlayerLeft(): SocketEvent {
        return SocketEvent.GameLifecycle.PlayerLeft
    }

    fun sendMatchRequest() {
        val message = OutgoingMessage(
            action = SocketActions.FIND_MATCH,
            data = null,
        )
        webSocket?.send(jsonConverter.toJson(message))
    }

    fun sendChatMessage(message: String) {
        val request = OutgoingMessage(
            action = SocketActions.CHAT,
            data = Chat(
                message = message
            )
        )

        webSocket?.send(jsonConverter.toJson(request))
    }
}

