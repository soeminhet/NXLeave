package com.smh.nxleave.domain.repository

import java.io.File

interface StorageRepository {
    suspend fun uploadImage(file: File): String
}