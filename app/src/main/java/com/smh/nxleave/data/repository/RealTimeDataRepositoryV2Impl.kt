package com.smh.nxleave.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.mapper.toEventModel
import com.smh.nxleave.data.mapper.toLeaveRequestModel
import com.smh.nxleave.data.mapper.toStaffModel
import com.smh.nxleave.data.remote.firestore.FireStoreRemoteDataSource
import com.smh.nxleave.domain.model.EventModel
import com.smh.nxleave.domain.model.StaffModel
import com.smh.nxleave.domain.repository.RealTimeDataRepositoryV2
import com.smh.nxleave.screen.model.LeaveRequestModel
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

    override fun relatedStaffBy(projectIds: List<String>): Flow<List<StaffModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getRTStavesBy(projectIds)
                .addSnapshotListener { value, error ->
                    launch {
                        send(
                            value?.documents
                                ?.mapNotNull { it.data }
                                ?.map { it.toStaffModel() }
                                .orEmpty()
                        )
                    }
                }

            awaitClose{ listener.remove() }
        }
    }

    override fun getLeaveRequestBy(staffIds: List<String>): Flow<List<LeaveRequestModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getLeaveRequestBy(staffIds) {
                it.map { snapshot ->
                    snapshot?.documents?.map { document -> document.toLeaveRequestModel() }
                        .orEmpty()
                }.onSuccess {
                    launch {
                        send(it)
                    }
                }
            }
            awaitClose { listener.remove() }
        }
    }

    override fun getAllUpcomingEvent(): Flow<List<EventModel>> {
        return callbackFlow {
            val listener = fireStoreRemoteDataSource.getAllUpcomingEvents {
                it.map { snapshot ->
                    snapshot?.documents?.map { document -> document.toEventModel() }.orEmpty()
                }.onSuccess { events ->
                    launch {
                        send(events)
                    }
                }
            }
            awaitClose{ listener.remove() }
        }
    }
}