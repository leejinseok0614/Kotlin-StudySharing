package org.studysharing.studysharing.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.model.PushDTO
import org.studysharing.studysharing.network.NetworkModule

object Push {

    const val TAG = "Push"

    fun sendPush(destinationUid: String, title: String, message: String) {
        firestore?.collection("pushtokens")
            ?.document(destinationUid)
            ?.get()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result.get("pushToken").toString()
                    val pushDTO = PushDTO(
                        token,
                        PushDTO.Notification(
                            title,
                            message
                        )
                    )

                    CoroutineScope(IO).launch {
                        try {
                            val retrofit = NetworkModule.getRetrofitClient(Val.PUSH_URL)
                            val response = NetworkModule.getPushApi(retrofit).sendMessage(pushDTO)

                            if (response.isSuccessful) {
                                Log.e(TAG, "Response success")
                            } else {
                                Log.e(TAG, "Response error")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(TAG, e.toString())
                        }
                    }
                }
            }
    }

}