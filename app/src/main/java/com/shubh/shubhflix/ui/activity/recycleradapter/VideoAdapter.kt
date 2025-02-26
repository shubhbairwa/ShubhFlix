package com.shubh.shubhflix.ui.activity.recycleradapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubh.shubhflix.data.apiresponse.Hit
import com.shubh.shubhflix.databinding.ItemVideoCardBinding
import com.shubh.shubhflix.ui.activity.diffcallback.VideoDiffCallback


class VideoAdapter : ListAdapter<Hit, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    init {
        setHasStableIds(true) // ✅ Keeps item positions stable
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.hashCode().toLong() // ✅ Unique stable ID
    }

    private var onItemClickListener: ((Hit) -> Unit)? = null
    fun setOnItemClickListener(listener: (Hit) -> Unit) {
        onItemClickListener = listener
    }


    inner class VideoViewHolder(private val binding: ItemVideoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Hit) {
            binding.tvNameOfVideo.text = video.user
            binding.tvVideoDuration.text = video.duration.toString()
            binding.tvVideoDesc.text = video.tags.toString()
            Glide.with(itemView.context).load(video.videos.tiny.thumbnail)
                .into(binding.ivThumbnailOfVideo)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(getItem(position))

            }
        }
    }
}
