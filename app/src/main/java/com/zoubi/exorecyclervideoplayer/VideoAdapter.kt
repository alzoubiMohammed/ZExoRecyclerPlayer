package com.zoubi.exorecyclervideoplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.zoubi.exorecyclervideoplayer.common.VideoData
import com.zoubi.exorecyclervideoplayer.common.VideoPlayerSetup
import com.zoubi.exorecyclervideoplayer.databinding.ItemVideoBinding

class VideoAdapter(val requestManager: RequestManager) :
    ListAdapter<VideoModel, VideoAdapter.VideoViewHolder>(ItemCallback) {

    companion object {
        val ItemCallback = object : DiffUtil.ItemCallback<VideoModel>() {

            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem.id == newItem.id
            }


            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem.videoUrl == newItem.videoUrl
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position), requestManager)
    }

    class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root),
        VideoPlayerSetup {
        lateinit var item: VideoModel
        fun bind(item: VideoModel, requestManager: RequestManager) {
            binding.root.tag = this
            this.item = item
            requestManager.load(item.Thumbnail).centerCrop().into(
                binding.videoThumbnail
            )

        }

        override fun attachment(): VideoData {
            return this.item.videoData!!
        }

        override fun videoContainer(): ViewGroup {
            return binding.videoContainer
        }

        override fun videoProgress(): View {
            return binding.videoProgress
        }

        override fun videoThumbnail(): ImageView {
            return binding.videoThumbnail
        }

    }
}