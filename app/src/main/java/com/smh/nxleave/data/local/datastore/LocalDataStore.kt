package com.smh.nxleave.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface LocalDataStore {
    val staffIdFlow: Flow<String>

    suspend fun updateStaffId(value: String)
}