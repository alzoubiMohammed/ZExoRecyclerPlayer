package com.zoubi.exorecyclervideoplayer.common

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

interface VideoPlayerSetup {
    fun videoContainer(): ViewGroup
    fun videoProgress(): View
    fun videoThumbnail(): ImageView
    fun attachment(): VideoData
}
