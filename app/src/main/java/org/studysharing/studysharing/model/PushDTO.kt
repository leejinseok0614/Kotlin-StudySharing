package org.studysharing.studysharing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushDTO(
    var to: String? = null,
    var notification: Notification = Notification()
) : Parcelable {
    @Parcelize
    data class Notification(
        var title: String? = null,
        var body: String? = null
    ) : Parcelable
}