package com.zoubi.library.exorecyclervideoplayer.common

import com.google.android.exoplayer2.source.MediaSource

data class VideoData(
    val mediaSource: MediaSource? = null,
    var currentPosition: Long? = 0L
)