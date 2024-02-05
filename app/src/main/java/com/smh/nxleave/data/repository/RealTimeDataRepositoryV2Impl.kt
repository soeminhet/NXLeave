package com.smh.nxleave.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.mapper.toStaffModel
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.RealTimeDataRepositoryV2
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeDataRepositoryV2Impl @Inject constructor(
    private val fireStoreRemoteDataSource: FireStoreRemoteDataSource,
    private val localDataStore: LocalDataStore,
): RealTimeDataRepositoryV2 {
    override fun currentStaff(): Flow<StaffModel> {
        return callbackFlow {
            var listener: ListenerRegistration? = null
            localDataStore.staffIdFlow
                .filterNot { it.isEmpty() }
                .collectLatest {
                    listener?.remove()
                    listener = fireStoreRemoteDataSource.getRTStaffBy(it).addSnapshotListener { value, _ ->
                        value?.data?.toStaffModel()?.let { staff ->
                            launch {
                                send(staff)
                            }
                        }
                    }
                }

            awaitClose { listener?.remove() }
        }
    }
}