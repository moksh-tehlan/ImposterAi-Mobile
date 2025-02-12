package com.moksh.imposterai.data.entity

import com.moksh.imposterai.data.entity.request.SocketActions
import com.moksh.imposterai.data.entity.request.SocketEvents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsMessage<T>(
    @SerialName("action")
    val action: SocketEvents,
    val data: T? = null,
)

data class WsRequest<T>(
    val action: SocketActions,
    val data: T? = null,
)