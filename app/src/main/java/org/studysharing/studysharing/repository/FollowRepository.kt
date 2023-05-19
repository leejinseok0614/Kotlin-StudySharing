package org.studysharing.studysharing.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.model.FollowDTO

class FollowRepository {

    private val alarmRepository = AlarmRepository()
    var registration: ListenerRegistration? = null

    fun getAllWhereUid(uid: String, snapshotListener: EventListener<DocumentSnapshot>) {
        registration = firestore?.collection("users")
            ?.document(uid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun remove() {
        // query에 대한 listener registration 제거
        registration?.remove()
    }

    fun updateFollow(uid: String) {
        // 본인이 다른사람에게 팔로우시
        // 본인의 팔로잉 데이터 변경
        // 다른사람의 팔로우 데이터 변경
        val docFollowing = firestore?.collection("users")?.document(userUid!!)
        docFollowing?.let { dr ->
            firestore?.runTransaction { transaction ->
                var followDTO = transaction.get(dr).toObject(FollowDTO::class.java)

                if (followDTO == null) {
                    followDTO = FollowDTO().apply {
                        followingCount = 1
                        followings[uid] = true
                    }
                } else {
                    if (followDTO.followings.containsKey(uid)) {
                        // cancel following
                        followDTO.followingCount = followDTO.followingCount - 1
                        followDTO.followings.remove(uid)
                    } else {
                        // do following
                        followDTO.followingCount = followDTO.followingCount + 1
                        followDTO.followings[uid] = true
                    }
                }

                transaction.set(dr, followDTO)
            }

        }?.addOnSuccessListener {
            println("xxx updateFollow(following) : success!!")
        }

        val docFollower = firestore?.collection("users")?.document(uid)
        docFollower?.let { dr ->
            firestore?.runTransaction { transaction ->
                var followDTO = transaction.get(dr).toObject(FollowDTO::class.java)

                if (followDTO == null) {
                    followDTO = FollowDTO().apply {
                        followerCount = 1
                        followers[userUid!!] = true
                        alarmRepository.noticeFollow(uid)
                    }
                } else {
                    if (followDTO!!.followers.containsKey(userUid)) {
                        // cancel follow
                        followDTO!!.followerCount = followDTO!!.followerCount - 1
                        followDTO!!.followers.remove(userUid)
                    } else {
                        // do follow
                        followDTO!!.followerCount = followDTO!!.followerCount + 1
                        followDTO!!.followers[userUid!!] = true
                        alarmRepository.noticeFollow(uid)
                    }
                }

                transaction.set(dr, followDTO!!)
            }
        }?.addOnSuccessListener {
            println("xxx updateFollow(follower) : success!!")
        }
    }

}