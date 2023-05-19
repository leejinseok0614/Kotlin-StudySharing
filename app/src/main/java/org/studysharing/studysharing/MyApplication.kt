package org.studysharing.studysharing

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.studysharing.studysharing.util.Val

class MyApplication : Application() {

    companion object {

        lateinit var mInstance: MyApplication   // 싱글톤
        var auth: FirebaseAuth? = null  // firebase auth
        var firebaseStorage: FirebaseStorage? = null    // firebase storage
        var firestore: FirebaseFirestore? = null    // firebase store(db)
        var userUid: String? = null // current user's uid
        var userId: String? = null  // current user's id(email)

        fun getInstance(): MyApplication {
            return mInstance
        }

        fun requestPermission(activity: Activity) {
            val needPermissionList = checkPermission()

            if (needPermissionList.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    needPermissionList.toTypedArray(),
                    Val.REQ_PERMISSION
                )
            }
        }

        fun checkPermission(): MutableList<String> {
            val needPermissionList = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(
                    mInstance,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            return needPermissionList
        }
    }

    override fun onCreate() {
        mInstance = this
        auth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        super.onCreate()
    }

}