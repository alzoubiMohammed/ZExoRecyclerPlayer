package com.zoubi.exorecyclervideoplayer.common


import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


var videoPlayerSetup: VideoPlayerSetup? = null
private var playPosition = -1;
private var isVideoViewAdded = false
var AttachedView: View? by weakReference()
var notifyToResetLastVideo: Boolean? by weakReference(false)

data class MaxWithIndex(var index: Int, var max: Float)


fun RecyclerView.setupWithExoplayer(
    exoPlayer: ExoPlayer,
    playerView: StyledPlayerView,
    lifecycleOwner: LifecycleOwner
) {

    exoPlayer.setupWithPlayerViewAndLifecycle(playerView, lifecycleOwner)

    addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {

                RecyclerView.SCROLL_STATE_IDLE -> {

                    // this to reset last video when it Detached From Window  without lagging
                    if (notifyToResetLastVideo == true) {
                        notifyToResetLastVideo = false
                        exoPlayer.stopVideo(playerView)
                    }

                    val layoutManager = layoutManager as LinearLayoutManager
                    val firstPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    val list = arrayListOf<MaxWithIndex>()
                    //get all view holder that implement @VideoPlayerSetup
                    for (pos in firstPosition..lastPosition) {
                        val tag =
                            findViewHolderForAdapterPosition(pos)?.itemView?.tag
                     //if tag ==null  that mean viewHolder is not video and will ignore
                        if (tag != null) {
                            val view = layoutManager.findViewByPosition(pos)
                            view?.getPercentage { percentage ->
                                list.add(MaxWithIndex(pos, percentage))
                            }
                        }

                    }
                    // get the  largest view holder that take percentage in screen to play and need to be more then 60%
                    list.maxOfWithIndex { index, max ->

                        // to chek if video already play
                        if (playPosition == index || max < 60.toFloat()) {

                            if (max > 60.toFloat()) exoPlayer.playWhenReady = true
                            return@maxOfWithIndex
                        }
                     //to get the implement @VideoPlayerSetup
                        val tag =
                            findViewHolderForAdapterPosition(index)?.itemView?.tag

                        if (videoPlayerSetup != null) exoPlayer.stopVideo(
                            playerView
                        )

                        videoPlayerSetup = tag as VideoPlayerSetup
                        playPosition = index
                        exoPlayer.playVideo()
                        AttachedView = findViewHolderForAdapterPosition(index)?.itemView


                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = layoutManager as LinearLayoutManager
            val firstPosition = layoutManager.findFirstVisibleItemPosition()

            //if first feed on recyclerview is video @note implement the @VideoPlayerSetup
            if (firstPosition == 0 && playPosition == -1) {

                val view = layoutManager.findViewByPosition(firstPosition)
                view?.getPercentage { percentage ->
                    val tag =
                        findViewHolderForAdapterPosition(firstPosition)?.itemView?.tag

                    if (playPosition == firstPosition || percentage < 60.toFloat() || tag == null) return@getPercentage

                    if (videoPlayerSetup != null) exoPlayer.stopVideo(
                        playerView
                    )
                    videoPlayerSetup = tag as VideoPlayerSetup
                    playPosition = firstPosition
                    exoPlayer.playVideo()
                    AttachedView = findViewHolderForAdapterPosition(firstPosition)?.itemView


                }


            } else {
                if (videoPlayerSetup == null || playPosition == -1) return

                val view = layoutManager.findViewByPosition(playPosition)
                view?.getPercentage { percentage ->
                    if (percentage < 50.toFloat()) {
                        //stop video only and later will be remove if it Detached From Window or play anther video
                        exoPlayer.playWhenReady = false

                    }
                }
            }


        }
    })

    addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {

        override fun onChildViewAttachedToWindow(view: View) {
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            if (AttachedView != null && AttachedView == view) {
                notifyToResetLastVideo = true
                AttachedView = null
            }

        }

    })

}

fun ExoPlayer.bindToLifecycle(lifecycleOwner: LifecycleOwner, playerView: StyledPlayerView) {
    val observer = object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            if (videoPlayerSetup != null) stopVideo(
                playerView
            )
            release()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            playWhenReady = false
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            playWhenReady = true
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
}

fun ExoPlayer.setupWithPlayerViewAndLifecycle(
    playerView: StyledPlayerView,
    lifecycleOwner: LifecycleOwner
) {

    bindToLifecycle(lifecycleOwner, playerView)

    addListener(object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    if (videoPlayerSetup != null) {
                        videoPlayerSetup!!.videoProgress().visibility =
                            View.VISIBLE
                    }
                }
                Player.STATE_ENDED -> {

                    seekTo(0)
                }
                Player.STATE_IDLE -> {
                }
                Player.STATE_READY -> {

                    if (videoPlayerSetup != null) {
                        videoPlayerSetup!!.videoProgress().visibility =
                            View.GONE
                    }
                    if (!isVideoViewAdded) {
                        videoPlayerSetup?.videoContainer()?.addView(playerView)
                        videoPlayerSetup?.videoThumbnail()?.visibility =
                            View.GONE
                        videoPlayerSetup?.videoContainer()?.visibility = View.VISIBLE

                        isVideoViewAdded = true
                    }
                }
                else -> {
                }
            }
        }
    })


}

fun ExoPlayer.stopVideo(
    playerView: StyledPlayerView
) {
    stop()
    videoPlayerSetup?.attachment()?.currentPosition = currentPosition
    restViewHolder()

    if (playerView.parent == null) {
        return
    }
    val parent = playerView.parent as ViewGroup
    val index = parent.indexOfChild(playerView)
    if (index >= 0) {
        parent.removeViewAt(index)
    }
    isVideoViewAdded = false

}

fun ExoPlayer.playVideo() {
    setMediaSource(videoPlayerSetup?.attachment()?.mediaSource!!)
    playWhenReady = true
    prepare()
    seekTo(videoPlayerSetup?.attachment()?.currentPosition ?: 0L)

}

fun restViewHolder() {
    if (videoPlayerSetup != null) {
        videoPlayerSetup?.videoThumbnail()?.visibility =
            View.VISIBLE
        videoPlayerSetup?.videoProgress()?.visibility = View.GONE
        videoPlayerSetup?.videoContainer()?.visibility = View.INVISIBLE

    }
    videoPlayerSetup = null
    playPosition = -1
}

fun View.getPercentage(result: (percentage: Float) -> Unit) {
    val globalVisibleRect = Rect()
    getGlobalVisibleRect(globalVisibleRect)
    val itemVisibleRect = Rect()
    if (height > 0 && getGlobalVisibleRect(
            itemVisibleRect
        )
    ) {
        val visibilityExtent =
            if (itemVisibleRect.bottom >= globalVisibleRect.bottom) {
                val visibleHeight =
                    globalVisibleRect.bottom - itemVisibleRect.top
                Math.min(visibleHeight.toFloat() / height, 1f)
            } else {
                val visibleHeight =
                    itemVisibleRect.bottom - globalVisibleRect.top
                Math.min(visibleHeight.toFloat() / height, 1f)
            }
        result.invoke(visibilityExtent * 100)


    }
}

fun ArrayList<MaxWithIndex>.maxOfWithIndex(result: (position: Int, max: Float) -> Unit) {

    maxByOrNull {
        it.max
    }.also {
        it ?: return@also
        result.invoke(it.index, it.max)
    }


}

fun <T> weakReference(tIn: T? = null): ReadWriteProperty<Any?, T?> {
    return object : ReadWriteProperty<Any?, T?> {
        var t = WeakReference<T?>(tIn)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {

            return t.get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            t = WeakReference(value)
        }

    }
}

