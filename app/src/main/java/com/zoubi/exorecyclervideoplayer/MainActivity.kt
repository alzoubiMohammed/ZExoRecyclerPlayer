package com.zoubi.exorecyclervideoplayer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.zoubi.exorecyclervideoplayer.databinding.ActivityMainBinding
import com.zoubi.exorecyclervideoplayer.R
import com.zoubi.exorecyclervideoplayer.worker.VideoPreloadWorker
import com.zoubi.library.exorecyclervideoplayer.common.setupWithExoplayer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var videoAdapter: VideoAdapter
    lateinit var activityMainBinding: ActivityMainBinding


    @Inject
    lateinit var mCacheDataSourceFactory:CacheDataSource.Factory

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
        mediaObjectList.add(
            VideoModel(
                id = 0,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 1,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 2,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 3,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 4,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 5,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 6,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 7,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                data = mCacheDataSourceFactory
            )
        )
        mediaObjectList.add(
            VideoModel(
                id = 8,
                Thumbnail = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg",
                videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                data = mCacheDataSourceFactory
            )
        )
        schedulePreloadWork(mediaObjectList.map { it.videoUrl })

        videoAdapter.submitList(mediaObjectList)

    }

    private fun schedulePreloadWork(videoUrl: List<String>) {
        val workManager = WorkManager.getInstance(applicationContext)
        val videoPreloadWorker = VideoPreloadWorker.buildWorkRequest(videoUrl)
        workManager.enqueueUniqueWork(
            "VideoPreloadWorker",
            ExistingWorkPolicy.KEEP,
            videoPreloadWorker
        )
    }

}