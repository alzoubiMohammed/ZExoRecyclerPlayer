package com.zoubi.exorecyclervideoplayer

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.zoubi.library.exorecyclervideoplayer.common.VideoData

data class VideoModel(
    val id: Int,
    var videoData: VideoData? = null,
    val Thumbnail: String,
    val videoUrl: String,
    val userAgent: String
) {
    init {
        val item = MediaItem.Builder()
            .setTag(videoUrl)
            .setUri(Uri.parse(videoUrl))
            .build()
        val dataSource = DefaultHttpDataSource.Factory()
        dataSource.setUserAgent(userAgent)
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSource)
                .createMediaSource(item)
        videoData = VideoData(mediaSource, 0L)
    }
}