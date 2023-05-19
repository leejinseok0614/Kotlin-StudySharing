package org.studysharing.studysharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.studysharing.studysharing.CommentActivity
import org.studysharing.studysharing.MainActivity
import org.studysharing.studysharing.MyApplication.Companion.userUid
import org.studysharing.studysharing.OtherUserFragment
import org.studysharing.studysharing.R
import org.studysharing.studysharing.databinding.ItemDetailBinding
import org.studysharing.studysharing.model.PhotoContentDTO
import org.studysharing.studysharing.repository.ProfileRepository

interface DetailViewAdapterCallback {
    fun onChangeThumbStatus(position: Int, status: Boolean?)   // 좋아요, 싫어요 클릭
}

class DetailViewAdapter(val context: Context) :
    RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {

    var contents = listOf<PhotoContentDTO>()
    var contentUids = listOf<String>()
    val profileRepository = ProfileRepository()

    private var callback: DetailViewAdapterCallback? = null

    fun setDetailViewAdapterCallback(callback: DetailViewAdapterCallback) {
        this.callback = callback
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemDetailBinding.bind(itemView)

        init {
            binding.profileImg.setOnClickListener {
                // 프로필이미지
                val contentDTO = contents[adapterPosition]

                if (userUid == contentDTO.uid) {  // 마이페이지
                    (context as MainActivity).goUserFragment()
                } else {
                    val fragment = OtherUserFragment()
                        .apply {
                            arguments = Bundle().apply {
                                putString("uid", contentDTO.uid)
                                putString("userId", contentDTO.userId)
                            }
                        }
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit()
                }
            }

            binding.commentImg.setOnClickListener {
                // 댓글
                val intent = Intent(context, CommentActivity::class.java)
                    .apply {
                        putExtra("contentUid", contentUids[adapterPosition])
                        putExtra("destinationUid", contents[adapterPosition].uid)
                    }
                context.startActivity(intent)
            }
        }

        @SuppressLint("SetTextI18n")
        fun setBind(position: Int, item: PhotoContentDTO) {
            // Uid 를 이용해 profile 적용.
            profileRepository.getAllWhereUid(item.uid!!) { documentSnapshot, _ ->
                documentSnapshot?.let { ds ->
                    val url = ds.data?.get("images")
                    Glide.with(context)
                        .load(url ?: R.drawable.ic_profile)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profileImg)
                }
            }

            // 사진(게시물)
            Glide.with(context)
                .load(item.imgUrl)
                .into(binding.photoContentImg)

            binding.profileTv.text = item.userId
            binding.favoriteTv.text =
                "좋아요 ${item.favorites.count { it.value }}개, 싫어요 ${item.favorites.count { !it.value }}개"
            binding.explainTv.text = item.explain

            when (item.favorites[userUid]) {
                true -> {
                    binding.thumbUpImg.setImageResource(R.drawable.ic_baseline_thumb_up_alt_48)
                    binding.thumbDownImg.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_48)

                    binding.thumbUpImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, null)
                    }

                    binding.thumbDownImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, false)
                    }
                }

                false -> {
                    binding.thumbUpImg.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_48)
                    binding.thumbDownImg.setImageResource(R.drawable.ic_baseline_thumb_down_alt_48)

                    binding.thumbUpImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, true)
                    }

                    binding.thumbDownImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, null)
                    }
                }

                else -> {
                    binding.thumbUpImg.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_48)
                    binding.thumbDownImg.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_48)

                    binding.thumbUpImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, true)
                    }

                    binding.thumbDownImg.setOnClickListener {
                        callback?.onChangeThumbStatus(position, false)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = contents[position]

//        profiles.forEach {
//            if(it.uid == content.uid) {
//                holder.setBind(contents[position], it.photoUri)
//                return
//            }
//        }

        holder.setBind(position, contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemId(position: Int): Long {
        return contents[position].timestamp ?: position.toLong()
    }

}