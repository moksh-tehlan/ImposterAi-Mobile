package com.moksh.imposterai.data.utils

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.entity.response.ErrorResponse
import com.moksh.imposterai.data.entity.response.GenericResponse
import com.moksh.imposterai.domain.utils.DataError
import com.moksh.imposterai.domain.utils.Result
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

inline fun <T> safeCall(
    call: () -> Response<GenericResponse<T>>
): Result<GenericResponse<T>, DataError> {
    return try {
        val response = call()
        responseToResult(response)
    } catch (e: Exception) {
        when (e) {
            is SocketTimeoutException -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
            is JsonSyntaxException -> Result.Error(DataError.Network.SERVER_ERROR)
            else -> Result.Error(DataError.Network.UNKNOWN)
        }.also { e.printStackTrace() }
    }
}

fun <T> responseToResult(response: Response<GenericResponse<T>>): Result<GenericResponse<T>, DataError.Network> {
    Log.d("response: ",response.toString())
    if (response.isSuccessful) {
        val body = response.body()
        return if (body != null) {
            Result.Success(body)
        } else {
            Result.Error(DataError.Network.EMPTY_RESPONSE)
        }
    }

    val errorBody = response.errorBody()
    Log.d("ErrorBody object", errorBody?.toString() ?: "null")
    val errorBodyString = errorBody?.string()
    Log.d("ErrorBody string", errorBodyString ?: "null")
    val errorResponse = if (!errorBodyString.isNullOrEmpty()) {
        try {
            JsonConverter.fromJson<ErrorResponse>(errorBodyString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else null

    Log.d("ErrorResponse", errorResponse.toString())

    if (errorResponse == null) return Result.Error(DataError.Network.UNKNOWN)
    return when (errorResponse.status) {
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        4003 -> Result.Error(DataError.Network.ACCOUNT_NOT_VERIFIED)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}
