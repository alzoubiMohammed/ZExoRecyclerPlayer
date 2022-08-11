package com.zoubi.library.exorecyclervideoplayer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.Util
import com.zoubi.library.exorecyclervideoplayer.common.setupWithExoplayer
import com.zoubi.library.exorecyclervideoplayer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var videoAdapter: VideoAdapter
    lateinit var activityMainBinding: ActivityMainBinding


    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var playerView: StyledPlayerView

    @Inject
    lateinit var requestManager: RequestManager
    val mediaObjectList: ArrayList<VideoModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        videoAdapter = VideoAdapter(requestManager);
        activityMainBinding.videoRecyclerView.layoutManager = LinearLayoutManager(this)
        activityMainBinding.videoRecyclerView.setHasFixedSize(true)
        activityMainBinding.videoRecyclerView.adapter = videoAdapter
        activityMainBinding.videoRecyclerView.setupWithExoplayer(exoPlayer, playerView, this)

        prepareVideoList()

    }

    private fun prepareVideoList() {
        val agent = Util.getUserAgent(context, this.getString(R.string.app_name))
        mediaObjectList.add(
            VideoModel(
                id = 0,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 1,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 2,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 3,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 4,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 5,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 6,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 7,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                userAgent = agent
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 8,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                userAgent = agent
            )
        )

        videoAdapter.submitList(mediaObjectList)
    }

}