package org.studysharing.studysharing.repository

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import org.studysharing.studysharing.MyApplication
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.MyApplication.Companion.userId
import org.studysharing.studysharing.model.AlarmDTO
import org.studysharing.studysharing.util.Push
import org.studysharing.studysharing.util.Val

class AlarmRepository {

    var registration: ListenerRegistration? = null

    fun remove() {
        // query에 대한 listener registration 제거
        registration?.remove()
    }

    fun getAll(destinationUid: String, snapshotListener: EventListener<QuerySnapshot>) {
        registration = firestore?.collection("alarms")
            ?.whereEqualTo("destinationUid", destinationUid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun noticeFavorite(destinationUid: String) {
        if (destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                userId,
                MyApplication.userUid,
                Val.ALARM_FAVORITE,
                null,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
            val message = "$userId 님이 회원님의 스토리를 좋아합니다."
            Push.sendPush(destinationUid, "Instagram Clone", message)
        }
    }

    fun noticeComment(destinationUid: String, message: String) {
        if (destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                userId,
                MyApplication.userUid,
                Val.ALARM_COMMENT,
                message,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
            val message = "$userId 님이 회원님의 스토리에 댓글을 남겼습니다."
            Push.sendPush(destinationUid, "Instagram Clone", message)
        }
    }

    fun noticeFollow(destinationUid: String) {
        if (destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                userId,
                MyApplication.userUid,
                Val.ALARM_FOLLOW,
                null,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
            val message = "$userId 님이 회원님을 팔로우하기 시작했습니다."
            Push.sendPush(destinationUid, "Instagram Clone", message)
        }
    }

}