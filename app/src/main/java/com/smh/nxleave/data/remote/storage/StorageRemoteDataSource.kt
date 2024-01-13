package com.smh.nxleave.data.remote.storage

import android.net.Uri
import java.io.File

interface StorageRemoteDataSource {
    suspend fun uploadImage(file: File): Uri
}