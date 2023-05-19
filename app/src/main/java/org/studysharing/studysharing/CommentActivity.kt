package org.studysharing.studysharing

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.studysharing.studysharing.adapter.CommentAdapter
import org.studysharing.studysharing.databinding.ActivityCommentBinding
import org.studysharing.studysharing.model.PhotoContentDTO
import org.studysharing.studysharing.util.BaseActivity
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel

class CommentActivity : BaseActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var photoContentVm: PhotoContentViewModel

    private lateinit var commentAdapter: CommentAdapter

    private var contentUid: String? = null
    private var destinationUid: String? = null

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        commentAdapter.profileRepository.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        photoContentVm = ViewModelProvider(this)[PhotoContentViewModel::class.java]
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        println("xxx contentUid : $contentUid")
        println("xxx destinationUid: $destinationUid")

        setUi()
        observeComments()
    }

    private fun setUi() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.sendBtn.setOnClickListener {
            contentUid?.let { contentUid ->
                photoContentVm.insertComment(
                    contentUid,
                    destinationUid!!,
                    binding.commentEt.text.toString()
                )
                binding.commentEt.setText("")
            }
        }

        commentAdapter = CommentAdapter(this)
            .apply { setHasStableIds(true) }
        binding.commentRv.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(this@CommentActivity)
        }

        contentUid?.let { uid ->
            photoContentVm.getAllComment(uid)
        }
    }

    private fun observeComments() {
        println("xxx observeComments() from CommentActivity")
        photoContentVm.commentLiveData.observe(this) { items ->
            if (items.isNotEmpty()) {
                commentAdapter.comments = items as ArrayList<PhotoContentDTO.Comment>
                commentAdapter.notifyDataSetChanged()
            }
        }
    }

}