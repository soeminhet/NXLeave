package com.smh.nxleave.data.remote.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface AuthRemoteDataSource {
    fun signIn(email: String, password: String, onResult: (Task<AuthResult>) -> Unit)
    fun signUp(email: String, password: String, onResult: (Task<AuthResult>) -> Unit)
    fun signOut()
    fun resetPassword(email: String, onResult: (Task<Void>) -> Unit, onFailure: (Exception) -> Unit)
}