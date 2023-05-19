package org.studysharing.studysharing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.studysharing.studysharing.R
import org.studysharing.studysharing.databinding.ItemCommentBinding
import org.studysharing.studysharing.model.PhotoContentDTO
import org.studysharing.studysharing.repository.ProfileRepository

class CommentAdapter(private val context: Context) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var comments: ArrayList<PhotoContentDTO.Comment> = arrayListOf()
    val profileRepository = ProfileRepository()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCommentBinding.bind(itemView)

        fun setBind(data: PhotoContentDTO.Comment, profileUrl: String?) {
            profileRepository.getAllWhereUid(data.uid!!) { documentSnapshot, _ ->
                documentSnapshot?.let { ds ->
                    val url = ds.data?.get("images")
                    Glide.with(context)
                        .load(url ?: R.drawable.ic_profile)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profileImg)
                }
            }

            val fontColor = Integer.toHexString(ContextCompat.getColor(context, R.color.black))
                .removeRange(0, 2)
            val content = HtmlCompat.fromHtml(
                "<font color=#${fontColor}><b>${data.userId} </b></font>" +
                        "<font color=#${fontColor}>${data.comment}</cont>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.contentTv.text = content
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBind(comments[position], null)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemId(position: Int): Long {
        return comments[position].timestamp ?: position.toLong()
    }

}