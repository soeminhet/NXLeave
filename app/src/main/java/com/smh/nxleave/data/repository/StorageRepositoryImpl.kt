package com.smh.nxleave.data.repository

import com.smh.nxleave.data.remote.storage.StorageRemoteDataSource
import com.smh.nxleave.domain.repository.StorageRepository
import java.io.File
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageRemoteDataSource: StorageRemoteDataSource
): StorageRepository {
    override suspend fun uploadImage(file: File): String {
        return storageRemoteDataSource.uploadImage(file).toString()
    }
}