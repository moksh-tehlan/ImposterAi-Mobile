package com.moksh.imposterai.di

import android.content.Context
import android.util.Log
import com.moksh.imposterai.BuildConfig
import com.moksh.imposterai.core.JsonConverter
import com.moksh.imposterai.data.api.AuthApi
import com.moksh.imposterai.data.api.GameApi
import com.moksh.imposterai.data.httpInterceptor.AuthInterceptor
import com.moksh.imposterai.data.local.SharedPreferencesManager
import com.moksh.imposterai.data.local.TokenManager
import com.moksh.imposterai.data.websocket.WebSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideAuthInterceptor(
        sharedPreferencesManager: SharedPreferencesManager,
        tokenManager: TokenManager,
    ): AuthInterceptor {
        return AuthInterceptor(
            sharedPreferencesManager,
            tokenManager,
        )
    }

    @Provides
    @Singleton
    fun providesHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
        return okHttpClient
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient
    ): Retrofit {
        Log.d("BaseURL", BuildConfig.BASE_URL)
        return Retrofit.Builder()
            .baseUrl("https://" + BuildConfig.BASE_URL)
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
            socketUrl = "wss://" + BuildConfig.BASE_URL + "/game",
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

    @Provides
    @Singleton
    fun provideTokenManager(): TokenManager {
        return TokenManager()
    }
}