package com.smh.nxleave.di

import com.smh.nxleave.data.repository.AuthRepositoryImpl
import com.smh.nxleave.data.repository.FireStoreRepositoryImpl
import com.smh.nxleave.data.repository.RealTimeDataRepositoryImpl
import com.smh.nxleave.data.repository.StorageRepositoryImpl
import com.smh.nxleave.domain.repository.AuthRepository
import com.smh.nxleave.domain.repository.FireStoreRepository
import com.smh.nxleave.domain.repository.RealTimeDataRepository
import com.smh.nxleave.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Singleton
    @Binds
    abstract fun bindFireStoreRepository(
        fireStoreRepositoryImpl: FireStoreRepositoryImpl
    ): FireStoreRepository

    @Singleton
    @Binds
    abstract fun bindStorageRepository(
        storageRepositoryImpl: StorageRepositoryImpl
    ): StorageRepository

    @Singleton
    @Binds
    abstract fun bindRealTimeDataRepositoryV2(
        realTimeDataRepositoryV2Impl: RealTimeDataRepositoryImpl
    ): RealTimeDataRepository
}