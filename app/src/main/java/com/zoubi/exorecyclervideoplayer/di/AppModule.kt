package com.zoubi.exorecyclervideoplayer.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.zoubi.exorecyclervideoplayer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module()
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideExoplayerInstance(
        @ApplicationContext context: Context,
        dataSource: CacheDataSource.Factory
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSource))
            .build()
    }

    @Singleton
    @Provides
    fun providePlayerView(
        @ApplicationContext context: Context, exoPlayer: ExoPlayer
    ): StyledPlayerView {
        val playerView = StyledPlayerView(context)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        playerView.useController = false
        playerView.player = exoPlayer
        return playerView
    }

    @Provides
    @Singleton
    fun provideAgent(@ApplicationContext context: Context): String {
        return Util.getUserAgent(context, "ExoRecyclerVideoplayer")
    }

    @Provides
    @Singleton
    fun provideDefaultHttpDataSourceFactory(agent: String): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
            .setUserAgent(agent)
            .setAllowCrossProtocolRedirects(true)
    }

    @Provides
    @Singleton
    fun provideCacheDataSourceFactory(mHttpDataSourceFactory: DefaultHttpDataSource.Factory,cache:SimpleCache): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
//            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }


    
    @Provides
    @Singleton
    fun provideRecentlyUsedCacheEvictor(): LeastRecentlyUsedCacheEvictor {
        return LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024)
    }

    @Provides
    @Singleton
    fun provideStandaloneDatabase(@ApplicationContext context: Context): StandaloneDatabaseProvider {
        return StandaloneDatabaseProvider(context)
    }

    @Provides
    @Singleton
    fun provideSimpleCache(
        @ApplicationContext context: Context,
        cacheEvictor: LeastRecentlyUsedCacheEvictor,
        exoplayerDatabaseProvider: StandaloneDatabaseProvider
    ): SimpleCache {
        return SimpleCache(context.cacheDir, cacheEvictor, exoplayerDatabaseProvider)
    }



    @Provides
    @Singleton
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.placeholder)

    }


    @Provides
    @Singleton
    fun provideGlideInstance(
        @ApplicationContext context: Context,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(context)
            .setDefaultRequestOptions(requestOptions)

    }


}