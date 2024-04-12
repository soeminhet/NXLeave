package com.smh.nxleave.data.repository

import com.smh.nxleave.data.local.datastore.LocalDataStore
import com.smh.nxleave.data.remote.auth.AuthRemoteDataSource
import com.smh.nxleave.domain.exception.FirebaseAuthCreateFail
import com.smh.nxleave.domain.exception.FirebaseAuthUserNull
import com.smh.nxleave.domain.model.AuthUserModel
import com.smh.nxleave.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val localDataStore: LocalDataStore,
): AuthRepository {
    override var cacheStaffId: String = ""

    override val staffIdFlow: Flow<String>
        get() = localDataStore.staffIdFlow.onEach { cacheStaffId = it }

    override fun signIn(email: String, password: String, onResult: (Result<AuthUserModel>) -> Unit) {
        authRemoteDataSource.signIn(email, password) { task ->
            if (task.isSuccessful) {
                val currentUser = task.result.user?.run {
                    AuthUserModel(
                        id = uid,
                        email = email
                    )
                }
                if (currentUser != null) {
                    cacheStaffId = currentUser.id
                    onResult(Result.success(currentUser))
                }
                else onResult(Result.failure(FirebaseAuthUserNull))
                authRemoteDataSource.signOut()
            } else {
                onResult(Result.failure(task.exception ?: FirebaseAuthCreateFail))
            }
        }
    }

    override fun singUp(email: String, password: String, onResult: (Result<AuthUserModel>) -> Unit) {
        authRemoteDataSource.signUp(email, password) { task ->
            if (task.isSuccessful) {
                val currentUser = task.result.user?.run {
                    AuthUserModel(
                        id = uid,
                        email = email
                    )
                }
                if (currentUser != null) onResult(Result.success(currentUser))
                else onResult(Result.failure(FirebaseAuthUserNull))
                authRemoteDataSource.signOut()
            } else {
                onResult(Result.failure(task.exception ?: FirebaseAuthCreateFail))
            }
        }
    }

    override suspend fun updateStaffId(value: String) {
        localDataStore.updateStaffId(value)
    }

    override fun resetPassword(email: String, onResult: (Result<Unit>) -> Unit) {
        authRemoteDataSource.resetPassword(
            email = email,
            onResult = { onResult(Result.success(Unit)) },
            onFailure = { onResult(Result.failure(it)) }
        )
    }
}