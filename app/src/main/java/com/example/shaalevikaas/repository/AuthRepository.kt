package com.example.shaalevikaas.repository

import com.example.shaalevikaas.model.Alumni
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn() = auth.currentUser != null

    suspend fun login(email: String, pass: String) = auth.signInWithEmailAndPassword(email, pass).await()

    suspend fun registerAlumni(alumni: Alumni, pass: String) {
        val result = auth.createUserWithEmailAndPassword(alumni.email, pass).await()
        val uid = result.user?.uid ?: throw Exception("User creation failed")
        val finalAlumni = alumni.copy(uid = uid)
        firestore.collection("alumni").document(uid).set(finalAlumni).await()
    }

    fun getAlumniFlow(uid: String): Flow<Alumni?> = callbackFlow {
        val subscription = firestore.collection("alumni").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(Alumni::class.java))
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun logout() = auth.signOut()
}
