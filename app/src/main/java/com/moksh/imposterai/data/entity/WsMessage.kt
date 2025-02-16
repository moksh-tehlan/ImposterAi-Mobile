package com.moksh.imposterai.data.entity

import com.moksh.imposterai.data.entity.request.SocketActions
import com.moksh.imposterai.data.entity.request.SocketEvents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class WebSocketMessage<T> {
    @Serializable
    data class WsMessage<T>(
        @SerialName("action")
        val action: SocketEvents,
        val data: T? = null,
    ) : WebSocketMessage<T>()

    @Serializable
    data class WsRequest<T>(
        @SerialName("action")
        val action: SocketActions,
        val data: T? = null,
    ) : WebSocketMessage<T>()
}

// Type aliases for better readability
typealias IncomingMessage<T> = WebSocketMessage.WsMessage<T>
typealias OutgoingMessage<T> = WebSocketMessage.WsRequest<T>