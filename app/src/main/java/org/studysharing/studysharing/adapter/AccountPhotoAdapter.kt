package org.studysharing.studysharing.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.studysharing.studysharing.model.PhotoContentDTO

class AccountPhotoAdapter(context: Context) :
    RecyclerView.Adapter<AccountPhotoAdapter.ViewHolder>() {

    var mContext = context
    var contents = listOf<PhotoContentDTO>()

    inner class ViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        fun setBind(content: PhotoContentDTO) {
            Glide.with(mContext)
                .load(content.imgUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val width = parent.context.resources.displayMetrics.widthPixels / 3
        val imageView = ImageView(mContext).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        }

        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBind(contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemId(position: Int): Long {
        return contents[position].timestamp ?: 0
    }

}