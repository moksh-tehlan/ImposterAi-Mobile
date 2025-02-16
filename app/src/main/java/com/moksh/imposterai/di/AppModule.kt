package com.moksh.imposterai.di

import android.content.Context
import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.api.AuthApi
import com.moksh.imposterai.data.api.GameApi
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.websocket.WebSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesHttpClient(sharedPreferencesManager: SharedPreferencesManager): OkHttpClient {
        val authToken = sharedPreferencesManager.getToken()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                if (!authToken.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $authToken")
                }
                requestBuilder.method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .addInterceptor(loggingInterceptor)
            .build()
        return okHttpClient
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient
    ): Retrofit {


        return Retrofit.Builder()
            .baseUrl("https://a282-43-230-107-128.ngrok-free.app")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWebSocketService(
        okHttpClient: OkHttpClient
    ): WebSocketService {
        return WebSocketService(
            okHttpClient = okHttpClient,
            socketUrl = "wss://a282-43-230-107-128.ngrok-free.app/game",
            jsonConverter = JsonConverter
        )
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGameApi(retrofit: Retrofit): GameApi {
        return retrofit.create(GameApi::class.java)
    }

    @Provides
    @Singleton
    fun providesSharedPref(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }
}