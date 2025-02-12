package com.moksh.imposterai.data.api

import com.moksh.imposterai.data.entity.request.GameResultRequest
import com.moksh.imposterai.data.entity.response.GameResultResponse
import com.moksh.imposterai.data.entity.response.GenericResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface GameApi {

    @POST("/game/result")
    fun checkResult(@Body gameResultRequest: GameResultRequest): GenericResponse<GameResultResponse>
}