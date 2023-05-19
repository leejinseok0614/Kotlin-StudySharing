package org.studysharing.studysharing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.studysharing.studysharing.model.FollowDTO
import org.studysharing.studysharing.repository.FollowRepository

class FollowViewModel : ViewModel() {
    // Repository
    private val followRepository = FollowRepository()

    // Follow
    private var _followLiveData = MutableLiveData<FollowDTO>()
    val followLiveData: LiveData<FollowDTO>
        get() = _followLiveData

    init {
        _followLiveData.value = FollowDTO()
    }

    /**
     * Query all data where 'uid'
     */
    fun getAllWhereUid(uid: String) {
        followRepository.getAllWhereUid(uid) { documentSnapshot, _ ->
            documentSnapshot?.let { ds ->
                val followDTO = ds.toObject(FollowDTO::class.java)
                followDTO?.let { dto ->
                    _followLiveData.value = dto
                }
            }
        }
    }

    /**
     * Update data
     */
    fun updateFollow(uid: String) {
        followRepository.updateFollow(uid)
    }

    /**
     * Remove listener registration
     */
    fun remove() {
        followRepository.remove()
    }

}