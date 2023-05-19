package org.studysharing.studysharing.repository

import android.net.Uri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import org.studysharing.studysharing.MyApplication.Companion.auth
import org.studysharing.studysharing.MyApplication.Companion.firebaseStorage
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.MyApplication.Companion.userId
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.model.PhotoContentDTO

class PhotoContentRepository {

    private val alarmRepository = AlarmRepository()
    var allRegistration: ListenerRegistration? =
        null   // getAll()에 대한 snapshotListener의 registration
    var uidRegistration: ListenerRegistration? =
        null   // getAllWhereUid()에 대한 snapshotListener의 registration
    var commentRegistration: ListenerRegistration? =
        null   // getAllComment()에 대한 snapshotListener의 registration

    fun getAll(snapshotListener: EventListener<QuerySnapshot>) {
        allRegistration = firestore?.collection("images")
            ?.orderBy("timestamp")
            ?.addSnapshotListener(snapshotListener)
    }

    fun getAllWhereUid(uid: String, snapshotListener: EventListener<QuerySnapshot>) {
        uidRegistration = firestore?.collection("images")
            ?.whereEqualTo("uid", uid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun getAllComment(contentUid: String, snapshotListener: EventListener<QuerySnapshot>) {
        commentRegistration = firestore?.collection("images")
            ?.document(contentUid)
            ?.collection("comments")
            ?.orderBy("timestamp")
            ?.addSnapshotListener(snapshotListener)
    }

    fun insert(fileName: String, uri: Uri, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("images")?.child(fileName)

        storageRef?.putFile(uri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
    }

    fun insertComment(contentUid: String, destinationUid: String, comment: String) {
        val commentDTO = PhotoContentDTO.Comment(
            userUid,
            userId,
            comment,
            System.currentTimeMillis()
        )
        firestore?.collection("images")?.document(contentUid)
            ?.collection("comments")?.document()
            ?.set(commentDTO)
        alarmRepository.noticeComment(destinationUid, comment)
    }

    fun updateFavorite(documentId: String, status: Boolean?) {
        val userId = auth?.currentUser?.uid
        assert(userId != null)

        val doc = firestore?.collection("images")?.document(documentId)
        doc?.let {
            if (status == null) {
                doc.update("favorites.${userId}", FieldValue.delete())
            } else {
                doc.update("favorites.${userId}", status)
            }
        }
    }

    fun remove() {
        // query에 대한 listener registration 제거
        allRegistration?.remove()
        uidRegistration?.remove()
    }

}