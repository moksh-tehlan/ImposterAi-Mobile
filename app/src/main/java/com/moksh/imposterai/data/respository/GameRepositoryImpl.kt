package com.moksh.imposterai.data.respository

import android.util.Log
import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.api.GameApi
import com.moksh.imposterai.data.entity.WsRequest
import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.entity.request.SocketActions
import com.moksh.imposterai.data.utils.safeCall
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.data.websocket.WebSocketService
import com.moksh.imposterai.domain.repository.GameRepository
import com.moksh.imposterai.domain.utils.DataError
import com.moksh.imposterai.domain.utils.EmptyResult
import com.moksh.imposterai.domain.utils.Result
import com.moksh.imposterai.domain.utils.asEmptyDataResult
import com.moksh.imposterai.domain.utils.map
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject


class GameRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService,
    private val gameApi: GameApi,
) : GameRepository {
    override suspend fun findMatch(): EmptyResult<DataError.Network> {
        webSocketService.connect()
        val findMatchMessage = WsRequest<Any>(SocketActions.FIND_MATCH)
        webSocketService.onSendMessage(JsonConverter.toJson(findMatchMessage))
        return Result.Success<Any>(Unit).asEmptyDataResult()
    }

    override suspend fun sendMessage(message: String): EmptyResult<DataError.Network> {
        val sendChat = WsRequest(SocketActions.CHAT, Chat(message))
        webSocketService.onSendMessage(JsonConverter.toJson(sendChat))
        return Result.Success<Any>(Unit).asEmptyDataResult()
    }

    override fun socketEvents(): SharedFlow<SocketEvent> {
        return webSocketService.eventFlow
    }

    override fun checkResult(gameResultRequest: GameResultRequest): Result<Boolean, DataError> {
        val result = safeCall {
            gameApi.checkResult(gameResultRequest)
        }
        return result.map { genericResponse ->
            genericResponse.data.correctAnswer
        }
    }
}

data class Chat(
    val message: String,
)