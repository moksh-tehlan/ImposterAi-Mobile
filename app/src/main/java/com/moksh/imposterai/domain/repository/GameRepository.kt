package com.moksh.imposterai.domain.repository

import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.websocket.SocketEvent
import com.moksh.imposterai.domain.utils.DataError
import com.moksh.imposterai.domain.utils.EmptyResult
import com.moksh.imposterai.domain.utils.Result
import kotlinx.coroutines.flow.SharedFlow

interface GameRepository {
    suspend fun findMatch(): EmptyResult<DataError.Network>
    suspend fun sendMessage(message: String): EmptyResult<DataError.Network>
    fun socketEvents(): SharedFlow<SocketEvent>
    suspend fun checkResult(gameResultRequest: GameResultRequest): Result<Boolean, DataError>
}