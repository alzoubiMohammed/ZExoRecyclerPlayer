package com.zoubi.exorecyclervideoplayer.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.Downloader
import com.google.android.exoplayer2.offline.ProgressiveDownloader
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltWorker
class VideoPreloadWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

    private var videoCachingJob: Job? = null


    @Inject
    lateinit var mCacheDataSource: CacheDataSource.Factory


    companion object {
        const val VIDEO_URLS = "video_urls"

        fun buildWorkRequest(yourParameter: List<String>): OneTimeWorkRequest {
            val data =
                Data.Builder().putStringArray(VIDEO_URLS, yourParameter.toTypedArray()).build()
            return OneTimeWorkRequestBuilder<VideoPreloadWorker>().apply { setInputData(data) }
                .build()
        }
    }


    @DelicateCoroutinesApi
    override fun doWork(): Result {
        return try {
            val videoUrl: List<String?> = inputData.getStringArray(VIDEO_URLS)!!.toList()

            videoCachingJob = GlobalScope.launch(Dispatchers.IO) {
                videoUrl.forEach {
                    preCacheVideo(it)
                }
            }


            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }


    private fun preCacheVideo(videoUrl: String?) {
        runCatching {

            val x = ProgressiveDownloader(
                MediaItem.Builder().setUri(videoUrl).build(),
                mCacheDataSource
            )

            x.download(
                Downloader.ProgressListener { contentLength, bytesDownloaded, percentDownloaded ->
                    Log.d("ProgressiveDownloader", "preCacheVideo: ${bytesDownloaded}")
                    if (bytesDownloaded > 1000000) x.cancel()
                })
        }.onFailure {
            Log.d("ProgressiveDownloader", "Cache Failure for position: $videoUrl")
        }.onSuccess {
            Log.d("ProgressiveDownloader", "Cache success for position: $videoUrl")

        }




    }


}