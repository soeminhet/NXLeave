package com.smh.nxleave.data.local.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalDataStoreImpl @Inject constructor(
    private val application: Application
): LocalDataStore{

    private val dataStore by lazy { application.applicationContext.dataStore }
    private val STAFF_ID = stringPreferencesKey("StaffId")

    override val staffIdFlow: Flow<String>
        get() = dataStore.data.map { settings -> settings[STAFF_ID] ?: "" }

    override suspend fun updateStaffId(value: String) {
        dataStore.edit { settings ->
            settings[STAFF_ID] = value
        }
    }
}