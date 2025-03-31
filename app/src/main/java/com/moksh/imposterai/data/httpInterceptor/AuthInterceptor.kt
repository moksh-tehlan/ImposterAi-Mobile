package com.moksh.imposterai.data.httpInterceptor

import com.moksh.imposterai.BuildConfig
import com.moksh.imposterai.data.api.AuthApi
import com.moksh.imposterai.data.entity.request.RefreshTokenRequest
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.local.TokenManager
import com.moksh.imposterai.data.utils.safeCall
import com.moksh.imposterai.domain.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class AuthInterceptor @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val tokenManager: TokenManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPreferencesManager.getAccessTokenToken()
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val newRequest = requestBuilder
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            val refreshToken = sharedPreferencesManager.getRefreshToken()
            if (refreshToken != null) {
                return handleRefreshToken(chain, originalRequest, refreshToken)
            } else {
                tokenManager.triggerLogout()
                return Response.Builder()
                    .request(originalRequest)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Authentication required")
                    .body(ResponseBody.create(null, ByteArray(0)))
                    .build();
            }
        }
        return response
    }

    private fun handleRefreshToken(
        chain: Interceptor.Chain,
        originalRequest: Request,
        refreshToken: String
    ): Response {
        synchronized(this) {
            val refreshedAccessToken = try {
                runBlocking(dispatcher) {
                    val okkHttp = OkHttpClient.Builder().build()
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://" + BuildConfig.BASE_URL)
                        .client(okkHttp)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val authApi = retrofit.create(AuthApi::class.java)
                    val refreshTokenResponse =
                        safeCall { authApi.refreshToken(RefreshTokenRequest(refreshToken)) }

                    when (refreshTokenResponse) {
                        is Result.Success -> {
                            val result = refreshTokenResponse.data.data
                            sharedPreferencesManager.saveTokens(
                                result.accessToken,
                                result.refreshToken
                            )
                            result.accessToken
                        }

                        is Result.Error -> {
                            null
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            if (refreshedAccessToken != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $refreshedAccessToken")
                    .build()
                return chain.proceed(newRequest)
            } else {
                tokenManager.triggerLogout()
                return Response.Builder()
                    .request(originalRequest)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Authentication required")
                    .body(ResponseBody.create(null, ByteArray(0)))
                    .build()
            }
        }

    }
}