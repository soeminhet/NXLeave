package com.smh.nxleave.data.remote.storage

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageRemoteDataSourceImpl @Inject constructor(): StorageRemoteDataSource {

    private val storageRef = Firebase.storage.reference

    override suspend fun uploadImage(file: File): Uri {
        return suspendCancellableCoroutine { continuation ->
            val uri = Uri.fromFile(file)
            val storageRef = storageRef.child("images/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    task.exception?.let {
                        continuation.resumeWithException(it)
                    }
                }
            }
        }
    }
}