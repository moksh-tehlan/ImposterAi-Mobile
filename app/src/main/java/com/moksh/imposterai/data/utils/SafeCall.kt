package com.moksh.imposterai.data.utils

import com.google.firebase.FirebaseNetworkException
import com.google.gson.JsonSyntaxException
import com.moksh.imposterai.domain.utils.DataError
import java.net.ConnectException
import java.net.SocketTimeoutException
import com.moksh.imposterai.domain.utils.Result
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
inline fun <reified T> safeCall(
    call: () -> T
): Result<T, DataError> {
    return try {
        when(val response = call()) {
            is Response<*> -> responseToResult(response as Response<T>)
            else -> Result.Success(response)
        }
    } catch (e: Exception) {
        when (e) {
            is SocketTimeoutException -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
            is ConnectException, is FirebaseNetworkException -> Result.Error(DataError.Network.NO_INTERNET)
            is JsonSyntaxException -> Result.Error(DataError.Network.SERVER_ERROR)
            else -> Result.Error(DataError.Local.UNKNOWN)
        }.also { e.printStackTrace() }
    }
}

fun <T> responseToResult(response: Response<T>): Result<T, DataError.Network> {
    return when (response.code()) {
        in 200..299 -> Result.Success(response.body()!!)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}