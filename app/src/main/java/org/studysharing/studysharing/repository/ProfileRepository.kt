package org.studysharing.studysharing.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.studysharing.studysharing.MyApplication.Companion.auth
import org.studysharing.studysharing.MyApplication.Companion.firebaseStorage
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.model.PhotoContentDTO

class ProfileRepository {

    var allRegistration: ListenerRegistration? = null
    var uidRegistration: ListenerRegistration? = null

    fun getAll(snapshotListener: EventListener<QuerySnapshot>) {
        allRegistration =
            firestore?.collection("profileImages")?.addSnapshotListener(snapshotListener)
    }

    fun getAllWhereUid(uid: String, snapshotListener: EventListener<DocumentSnapshot>) {
        uidRegistration = firestore?.collection("profileImages")?.document(uid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun insert(uid: String, imageUri: Uri, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("userProfileImages")?.child(uid)
        storageRef?.putFile(imageUri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
    }

    fun remove() {
        allRegistration?.remove()
        uidRegistration?.remove()
    }

    suspend fun getLevel() = withContext(Dispatchers.IO) {
        val userId = auth?.currentUser?.uid
        assert(userId != null)

        try {
            val snapshot = firestore?.collection("images")
                ?.whereEqualTo("uid", userId)
                ?.get()
                ?.await() ?: return@withContext ProfileLevel.BRONZE

            val contents =
                snapshot.documents.mapNotNull { it.toObject(PhotoContentDTO::class.java) }
            val likes = contents.sumOf { it.favorites.count { it.value } }
            val unlikes = contents.sumOf { it.favorites.count { !it.value } }
            val score = likes + unlikes
            Log.d("ProfileRepository", "Score: ${score}")

            if (score < 100) {
                return@withContext ProfileLevel.BRONZE
            } else if (score < 300) {
                return@withContext ProfileLevel.SILVER
            } else if (score < 500) {
                return@withContext ProfileLevel.GOLD
            } else {
                return@withContext ProfileLevel.DIAMOND
            }
        } catch (e: Exception) {
            return@withContext ProfileLevel.BRONZE
        }
    }
}

enum class ProfileLevel(val color: Int) {
    BRONZE(0xc49c48), SILVER(0xc0c0c0), GOLD(0xffd700), DIAMOND(0x005666)
}