package com.zoubi.exorecyclervideoplayer

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.zoubi.library.exorecyclervideoplayer.common.VideoData

data class VideoModel(
    val id: Int,
    var videoData: VideoData? = null,
    val Thumbnail: String,
    val videoUrl: String,
    val data: DataSource.Factory
) {
    init {
        val item = MediaItem.Builder()
            .setTag(videoUrl)
            .setUri(Uri.parse(videoUrl))
            .build()
//        val dataSource = DefaultHttpDataSource.Factory()

        val mediaSource =
            ProgressiveMediaSource.Factory(data)
                .createMediaSource(item)
        videoData = VideoData(mediaSource, 0L)
    }
}