package org.studysharing.studysharing.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.studysharing.studysharing.MyApplication.Companion.firestore
import org.studysharing.studysharing.model.PhotoContentDTO
import org.studysharing.studysharing.repository.PhotoContentRepository

class PhotoContentViewModel : ViewModel() {
    // Repository
    private val contentRepository = PhotoContentRepository()

    // Photo content
    private var _contentLiveData = MutableLiveData<List<PhotoContentDTO>>()
    val contentLiveData: LiveData<List<PhotoContentDTO>>
        get() = _contentLiveData

    // Photo content(uid)
    private var _uidContentLiveData = MutableLiveData<List<PhotoContentDTO>>()
    val uidContentLiveData: LiveData<List<PhotoContentDTO>>
        get() = _uidContentLiveData

    // Document id
    private var _contentIdLiveData = MutableLiveData<List<String>>()
    val contentIdLiveData: LiveData<List<String>>
        get() = _contentIdLiveData

    // Comment
    private var _commentLiveData = MutableLiveData<List<PhotoContentDTO.Comment>>()
    val commentLiveData: LiveData<List<PhotoContentDTO.Comment>>
        get() = _commentLiveData

    // Result of upload photo content
    private var _resultLiveData = MutableLiveData<Boolean>()
    val resultLiveData: LiveData<Boolean>
        get() = _resultLiveData

    init {
        _contentLiveData.value = arrayListOf()
        _contentIdLiveData.value = arrayListOf()
        _uidContentLiveData.value = arrayListOf()
        _resultLiveData.value = false
    }

    /**
     * Query all photo content data
     */
    fun getAll() {
        contentRepository.getAll { querySnapshot, _ ->
            val value = arrayListOf<PhotoContentDTO>()
            val idValue = arrayListOf<String>()

            querySnapshot?.let { qs ->
                for (snapshot in qs.documents) {
                    val item = snapshot.toObject(PhotoContentDTO::class.java)
                    idValue.add(snapshot.id)
                    value.add(item!!)
                }
            }

            _contentLiveData.value = value
            _contentIdLiveData.value = idValue
        }
    }

    /**
     * Query all photo content data where 'uid'
     */
    fun getAllWhereUid(uid: String) {
        contentRepository.getAllWhereUid(uid) { querySnapshot, _ ->
            val value = arrayListOf<PhotoContentDTO>()

            querySnapshot?.let { qs ->
                for (snapshot in qs.documents) {
                    val item = snapshot.toObject(PhotoContentDTO::class.java)
                    value.add(item!!)
                }
            }

            _uidContentLiveData.value = value
        }
    }

    /**
     * Query all comment where 'contentUid'
     */
    fun getAllComment(contentUid: String) {
        contentRepository.getAllComment(contentUid) { querySnapshot, _ ->
            val comments = arrayListOf<PhotoContentDTO.Comment>()

            querySnapshot?.let { qs ->
                println("xxx ${qs.size()}")
                for (snapshot in qs.documents) {
                    val item = snapshot.toObject(PhotoContentDTO.Comment::class.java)!!
                    comments.add(item)
                }
            }

            _commentLiveData.value = comments
        }
    }

    /**
     * Upload photo content
     */
    fun insert(fileName: String, uri: Uri, contentDTO: PhotoContentDTO) {
        contentRepository.insert(fileName, uri) {
            contentDTO.imgUrl = it.toString()
            firestore?.collection("images")?.document()?.set(contentDTO)
            _resultLiveData.value = true
        }
    }

    /**
     * Write comment
     */
    fun insertComment(contentUid: String, destinationUid: String, comment: String) {
        contentRepository.insertComment(contentUid, destinationUid, comment)
    }

    /**
     * Update favorite button
     */
    fun updateFavorite(documentId: String, like: Boolean?) {
        contentRepository.updateFavorite(documentId, like)
    }

    /**
     * Remove listener registration
     */
    fun remove() {
        contentRepository.remove()
    }

}