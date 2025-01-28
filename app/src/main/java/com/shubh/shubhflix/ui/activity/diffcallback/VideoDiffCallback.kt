package com.shubh.shubhflix.ui.activity.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.shubh.shubhflix.data.apiresponse.Hit

class VideoDiffCallback : DiffUtil.ItemCallback<Hit>() {
    override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
        return oldItem == newItem
    }
}
