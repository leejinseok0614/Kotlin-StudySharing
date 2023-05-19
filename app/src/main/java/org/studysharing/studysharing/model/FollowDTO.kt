package org.studysharing.studysharing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Follow data
 */
@Parcelize
data class FollowDTO(
    var followerCount: Int = 0, // 팔로워수
    var followers: MutableMap<String, Boolean> = mutableMapOf(),    // 팔로워한 사람들
    var followingCount: Int = 0,    // 팔로잉수
    var followings: MutableMap<String, Boolean> = mutableMapOf()    // 팔로잉한 사람들
) : Parcelable