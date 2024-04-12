package com.smh.nxleave.domain.repository

import com.smh.nxleave.domain.model.AuthUserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    var cacheStaffId: String
    val staffIdFlow: Flow<String>
    fun signIn(email: String, password: String, onResult: (Result<AuthUserModel>) -> Unit)
    fun singUp(email: String, password: String, onResult: (Result<AuthUserModel>) -> Unit)
    suspend fun updateStaffId(value: String)
    fun resetPassword(email: String, onResult: (Result<Unit>) -> Unit)
}