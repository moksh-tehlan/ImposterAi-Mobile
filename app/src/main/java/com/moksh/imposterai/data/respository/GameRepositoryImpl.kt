package com.moksh.imposterai.data.respository

import com.moksh.imposterai.data.api.GameApi
import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.entity.response.SocketEvent
import com.moksh.imposterai.data.utils.safeCall
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
        try {
            webSocketService.connect()
            webSocketService.sendMatchRequest()
            return Result.Success(Unit).asEmptyDataResult()
        } catch (e: Exception) {
            return Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun sendMessage(message: String): EmptyResult<DataError.Network> {
        try {
            webSocketService.sendChatMessage(message)
            return Result.Success(Unit).asEmptyDataResult()
        } catch (e: Exception) {
            return Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override fun socketEvents(): SharedFlow<SocketEvent> {
        return webSocketService.eventFlow
    }

    override suspend fun checkResult(gameResultRequest: GameResultRequest): Result<Boolean, DataError> {
        return safeCall {
            gameApi.checkResult(gameResultRequest)
        }.map { genericResponse ->
            genericResponse.data.isCorrectAnswer
        }
    }

    override fun disconnect() {
        webSocketService.disconnect()
    }
}

