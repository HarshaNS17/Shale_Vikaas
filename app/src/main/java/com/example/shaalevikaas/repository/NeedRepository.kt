package com.example.shaalevikaas.repository

import android.net.Uri
import com.example.shaalevikaas.model.Announcement
import com.example.shaalevikaas.model.Need
import com.example.shaalevikaas.model.Pledge
import com.example.shaalevikaas.model.Alumni
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NeedRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getNeeds(): Flow<List<Need>> = callbackFlow {
        val subscription = firestore.collection("needs")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Need::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    private suspend fun uploadImage(uri: Uri, path: String): String {
        return try {
            val ref = storage.reference.child(path)
            // Use putFile which handles Uri permissions better
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.localizedMessage}")
        }
    }

    suspend fun addNeed(need: Need, imageUri: Uri?, beforeUri: Uri?) {
        val doc = firestore.collection("needs").document()
        var updatedNeed = need.copy(id = doc.id, createdAt = Timestamp.now())
        
        try {
            imageUri?.let {
                val url = uploadImage(it, "needs/${doc.id}/main_${System.currentTimeMillis()}.jpg")
                updatedNeed = updatedNeed.copy(imageUrl = url)
            }
            
            beforeUri?.let {
                val url = uploadImage(it, "needs/${doc.id}/before_${System.currentTimeMillis()}.jpg")
                updatedNeed = updatedNeed.copy(beforeImage = url)
            }
            
            doc.set(updatedNeed).await()
        } catch (e: Exception) {
            throw Exception("Error saving project: ${e.localizedMessage}")
        }
    }

    suspend fun updateNeed(need: Need, imageUri: Uri?, afterUri: Uri?) {
        var updatedNeed = need
        
        try {
            imageUri?.let {
                val url = uploadImage(it, "needs/${need.id}/main_${System.currentTimeMillis()}.jpg")
                updatedNeed = updatedNeed.copy(imageUrl = url)
            }
            
            afterUri?.let {
                val url = uploadImage(it, "needs/${need.id}/after_${System.currentTimeMillis()}.jpg")
                updatedNeed = updatedNeed.copy(afterImage = url)
            }
            
            firestore.collection("needs").document(need.id).set(updatedNeed, SetOptions.merge()).await()
        } catch (e: Exception) {
            throw Exception("Error updating project: ${e.localizedMessage}")
        }
    }

    suspend fun submitPledge(pledge: Pledge) {
        val pledgeDoc = firestore.collection("pledges").document()
        val finalPledge = pledge.copy(pledgeId = pledgeDoc.id, timestamp = Timestamp.now())
        
        val needRef = firestore.collection("needs").document(pledge.needId)
        val alumniRef = firestore.collection("alumni").document(pledge.alumniId)

        try {
            firestore.runTransaction { transaction ->
                // Check if alumni document exists
                val alumniSnapshot = transaction.get(alumniRef)
                if (!alumniSnapshot.exists()) {
                    throw Exception("Alumni profile not found")
                }

                transaction.set(pledgeDoc, finalPledge)
                
                // Atomically increment project funding
                transaction.update(needRef, "pledgedAmount", FieldValue.increment(pledge.amount))
                
                // Atomically increment alumni contribution
                transaction.update(alumniRef, "donatedAmount", FieldValue.increment(pledge.amount))
            }.await()
        } catch (e: Exception) {
            throw Exception("Pledge transaction failed: ${e.localizedMessage}")
        }
    }

    fun getAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val subscription = firestore.collection("announcements")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Announcement::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addAnnouncement(announcement: Announcement, imageUri: Uri?) {
        var finalAnnouncement = announcement.copy(createdAt = Timestamp.now())
        try {
            imageUri?.let {
                val url = uploadImage(it, "announcements/${System.currentTimeMillis()}.jpg")
                finalAnnouncement = finalAnnouncement.copy(imageUrl = url)
            }
            firestore.collection("announcements").add(finalAnnouncement).await()
        } catch (e: Exception) {
            throw Exception("Error broadcasting announcement: ${e.localizedMessage}")
        }
    }

    fun getHallOfFame(): Flow<List<Alumni>> = callbackFlow {
        val subscription = firestore.collection("alumni")
            .orderBy("donatedAmount", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Alumni::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getAllAlumni(): Flow<List<Alumni>> = callbackFlow {
        val subscription = firestore.collection("alumni").orderBy("name")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Alumni::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getAllPledges(): Flow<List<Pledge>> = callbackFlow {
        val subscription = firestore.collection("pledges").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Pledge::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }
}
