package org.studysharing.studysharing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Profile data
 */
@Parcelize
data class ProfileDTO(
    var uid: String? = null,    // 사용자 uid
    var photoUri: String? = null    // 프로필이미지 url
) : Parcelable