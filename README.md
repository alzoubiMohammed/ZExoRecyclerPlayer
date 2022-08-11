# ZExoRecyclerPlayer [![](https://jitpack.io/v/alzoubiMohammed/ZExoRecyclerPlayer.svg)](https://jitpack.io/#alzoubiMohammed/ZExoRecyclerPlayer)

## Description

Android library for working with Exoplayer in RecyclerView

## Getting Started

### Dependencies

* Step 1. Add the JitPack repository to your build file:

```
allprojects {
	repositories {
	 ...
	 maven { url 'https://jitpack.io' }
		}
	}
```  

* Step 2. Add the dependency:

```
dependencies {

  implementation 'com.github.alzoubiMohammed:ZExoRecyclerPlayer:1.0.1'
	
	}
```  

### Usage

* Step 1. Add singleton Exoplayer and singleton PlayerView like
  {StyledPlayerView,PlayerControlView....} recommend to use 
  <!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[forks-url]:https://github.com/googlecodelabs/android-hilt Like:

```
@Module()
AppModule {
   ....
   
  @Provides
    fun provideExoplayerInstance(
        @ApplicationContext context: Context,
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
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
        ...
        return playerView
    }
    }
```  

* Step 2. Setup the RecycleView on Fragment Like :

```  
    class Fragment{
        @Inject
        lateinit var exoPlayer: ExoPlayer

        @Inject
        lateinit var playerView: StyledPlayerView
         ...
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         ...
         RecyclerViewWallInfo.setupWithExoplayer(
                exoPlayer,
                playerView,
                viewLifecycleOwner
            )
         
            }
      }
```  

* and on Activity Like :

```  
       class Activity{
         @Inject
         lateinit var exoPlayer: ExoPlayer

         @Inject
         lateinit var playerView: StyledPlayerView
          ...
        override fun onCreate(savedInstanceState: Bundle?) {
          ...
         RecyclerViewWallInfo.setupWithExoplayer(
                exoPlayer,
                playerView,
                this
            )
         
            }
      }

``` 

* Step 3.ViewHolder XML needs to be like:


```  
   <androidx.constraintlayout.widget.ConstraintLayout
        ...
             >
          <FrameLayout
             ... 
           android:id="@+id/videoContainer"
   
           />
             
          <androidx.appcompat.widget.AppCompatImageView
             ...
             android:id="@+id/videoThumbnail"
  
           />

         <ProgressBar
            ...
           android:id="@+id/videoProgress"
          />

   </androidx.constraintlayout.widget.ConstraintLayout>

``` 

* Step 4. finally ViewHolder need to implement the VideoPlayerSetup 
* and you can read more about [mediaSource] :https://exoplayer.dev/media-sources.html

``` 
  class VideoViewHolder(itemView:View) :
  RecyclerView.ViewHolder(itemView), VideoPlayerSetup {
     ...
     
    fun bind(videoItem:VideoItem){
      itemView.tag = this
       ...
    }
    
   override fun videoContainer(): ViewGroup {
            return itemView.videoContainer
        }

        override fun videoProgress(): View {
            return itemView.videoProgress
        }

        override fun videoThumbnail(): ImageView {
            return itemView.videoThumbnail
        }

        override fun attachment(): VideoData {
            return VideoData(videoItem.mediaSource,videoItem.currentPosition?:0L)
        }
  
  
     }

``` 


## License

This project is licensed under the [MIT License] License - see the LICENSE.md file for details
