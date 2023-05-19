package org.studysharing.studysharing.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Utils {

    companion object {
        private const val TAG = "Utils"

        // 키해시 출력
        @RequiresApi(Build.VERSION_CODES.P)
        fun printKeyHash(context: Context) {
            try {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                val signingInfo = packageInfo.signingInfo.apkContentsSigners

                for (signature in signingInfo) {
                    val messageDigest = MessageDigest.getInstance("SHA")
                    messageDigest.update(signature.toByteArray())

                    val keyHash = String(Base64.encode(messageDigest.digest(), 0))
                    Log.e(TAG, "xxx keyHash=$keyHash")
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
    }

}