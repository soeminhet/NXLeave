package com.smh.nxleave.di

import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.local.datastore.LocalDataStoreImpl
import com.smh.nxleave.data.remote.auth.AuthRemoteDataSource
import com.smh.nxleave.data.remote.auth.AuthRemoteDataSourceImpl
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSourceImpl
import com.smh.nxleave.data.remote.storage.StorageRemoteDataSource
import com.smh.nxleave.data.remote.storage.StorageRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindLocalDataStore(
        localDataStoreImpl: LocalDataStoreImpl
    ): LocalDataStore

    @Singleton
    @Binds
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindFireStoreRemoteDataSource(
        fireStoreRemoteDataSourceImpl: FireStoreRemoteDataSourceImpl
    ): FireStoreRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindStorageRemoteDataSource(
        storageRemoteDataSourceImpl: StorageRemoteDataSourceImpl
    ): StorageRemoteDataSource
}