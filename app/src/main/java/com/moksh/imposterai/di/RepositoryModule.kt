package com.moksh.imposterai.di

import com.moksh.imposterai.data.respository.AuthRepositoryImpl
import com.moksh.imposterai.data.respository.GameRepositoryImpl
import com.moksh.imposterai.domain.repository.AuthRepository
import com.moksh.imposterai.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun getAuthRepositoryImpl(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun getGameRepositoryImpl(gameRepositoryImpl: GameRepositoryImpl): GameRepository
}