package org.studysharing.studysharing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmDTO(
    var destinationUid: String? = null,
    var userId: String? = null,
    var uid: String? = null,
    var kind: Int? = null,
    var message: String? = null,
    var timestamp: Long? = null
) : Parcelable