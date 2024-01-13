package com.smh.nxleave.data.remote.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(): AuthRemoteDataSource {

    private val auth = Firebase.auth

    override fun signIn(email: String, password: String, onResult: (Task<AuthResult>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onResult)
    }

    override fun signUp(email: String, password: String, onResult: (Task<AuthResult>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(onResult)
    }

    override fun signOut() {
        auth.signOut()
    }
}