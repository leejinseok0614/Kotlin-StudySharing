package org.studysharing.studysharing.util

class Val {

    companion object {

        const val REQ_PERMISSION = 0    // 권한요청

        // Alarm flag
        const val ALARM_FAVORITE = 0    // 좋아요 알림
        const val ALARM_COMMENT = 1 // 댓글 알림
        const val ALARM_FOLLOW = 2  // 팔로우 알림

        // Push
        const val PUSH_URL = "https://fcm.googleapis.com"
        const val PUSH_CONTENT_TYPE = "application/json"
        const val PUSH_SERVER_KEY =
            "AAAAtaBse3s:APA91bHLnpdeDLEUGEFkqXbU0OcX7eIsHSPyWSKlH7xvfRGQeIfbR3ytAIb3W6C8ghRXj1aafK72jeVbG_Xf2TDYi1aTe5QZ2qqiFDpCG0E_7vEDv_Er9vcYYdKyWyXJ3R3jXKiaPL3M"

    }

}