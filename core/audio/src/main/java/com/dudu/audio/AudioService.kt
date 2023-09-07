package com.dudu.audio

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.dudu.common.ext.logD
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * 音频服务
 * 需要注意该服务可能会配置到其他进程运行，注意对象创建与关联
 * Created by Dzc on 2023/8/28.
 */
// setUseLazyPreparation、setMediaNotificationProvider 不是稳定的api所以加入该注解
//@UnstableApi
class AudioService : MediaLibraryService() {

    companion object {
        // 获取到通知操作
        const val ACTION_PLAY = "action_play"// 播放暂停
        const val ACTION_PREVIOUS = "action_previous" // 前一首
        const val ACTION_NEXT = "action_next" // 后一首

        // 发送通知更新key
        const val RECEIVER_TITLE = "receiver_title" // 通知更新title
        const val RECEIVER_PLAYING = "receiver_playing" // 播放状态更新

    }

    private val audioServiceScope = CoroutineScope(Dispatchers.Main)// 操作作用域

    // 解析媒体
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this)
            //.setUseLazyPreparation(true) //控制ExoPlayer是否应该使用延迟准备的方法。true时，ExoPlayer会在需要播放媒体时才进行准备，而不是在创建ExoPlayer实例时就立即进行准备。
            .setHandleAudioBecomingNoisy(true)// ExoPlayer会自动处理音频变得嘈杂的情况，例如当用户拔出耳机时，暂停正在播放的媒体内容。
            .setAudioAttributes(               // 设置AudioAttributes播放器将使用的以及是否处理音频焦点。如果应处理音频焦点，则usage必须是USAGE_MEDIA或USAGE_GAME
                AudioAttributes.Builder()       // 音频播放的属性
                    .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build(),
                true            // 音频焦点切换暂停处理
            )
            .build()
    }

    // 管理媒体内容的服务类，它允许应用程序将其媒体内容公开给其他应用程序，并提供媒体内容的浏览和检索功能
    private val mediaLibrarySession: MediaLibrarySession by lazy {
        MediaLibrarySession.Builder(this, exoPlayer, object : MediaLibrarySession.Callback {
            // MediaLibraryService 的作用在于此，获取媒体库中的信息，完成回调
            // 当前未实现
            override fun onGetLibraryRoot(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<MediaItem>> {
                return super.onGetLibraryRoot(session, browser, params)
            }

            override fun onGetItem(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                mediaId: String
            ): ListenableFuture<LibraryResult<MediaItem>> {
                return super.onGetItem(session, browser, mediaId)
            }

            override fun onGetChildren(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                parentId: String,
                page: Int,
                pageSize: Int,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                // 获取到当前媒体的播放列表，MediaItem需设置setIsBrowsable、setIsPlayable
                // 通常由本地保存播放列表
                val mediaItemList = List(exoPlayer.mediaItemCount) { index ->
                    exoPlayer.getMediaItemAt(index)
                }
                return Futures.immediateFuture(LibraryResult.ofItemList(mediaItemList, params))
            }

            @UnstableApi
            override fun onPlaybackResumption(
                mediaSession: MediaSession,
                controller: MediaSession.ControllerInfo
            ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                // 返回播放器的最近播放列表，当请求从媒体按钮接收器恢复播放或请求系统 UI 通知时，播放器应准备该播放列表。
                "onPlaybackResumption".logD()
                return super.onPlaybackResumption(mediaSession, controller)
            }


        })
            .setSessionActivity(
                // 设置一个启动此会话的用户界面的意图。这可以用作快速链接到正在进行的媒体屏幕。该意图应该是一个可以使用Activity＃startActivity（Intent）启动的活动。
                // 下面getLaunchIntentForPackage，返回一个该包下面的启动界面
                // PendingIntent.FLAG_IMMUTABLE表示创建一个不可变的PendingIntent对象，而PendingIntent.FLAG_UPDATE_CURRENT表示如果PendingIntent已经存在，则使用新的Intent更新该PendingIntent。
                packageManager.getLaunchIntentForPackage(packageName).let { sessionIntent ->
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                })
            .build()
    }

    // 服务session回调
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession

    @UnstableApi
    override fun onCreate() {
        super.onCreate()

        // 设置play的配置项，播放属性，音量，播放模式

        // 服务端监听播放状态
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // 切换媒体
                "AudioService.onMediaItemTransition: ${mediaItem?.mediaId}".logD()
                sendUpdateWidgetReceive()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 播放状态切换
                "AudioService.onIsPlayingChanged: $isPlaying".logD()
                sendUpdateWidgetReceive()
            }
        })
        // MediaBrowser.Builder(this, mediaLibrarySession.token).buildAsync()


        // 通知设置 有问题
        setMediaNotificationProvider(AudioNotificationProvider())

    }

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 每次startService都会执行
        action(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    @UnstableApi
    private fun action(intent: Intent?) {
        intent?.let {
            "AudioService ${it.action}".logD()
            when (it.action) {
                ACTION_PLAY -> {
                    if (exoPlayer.mediaItemCount == 0) {
                        // 添加本地数据
                        "AudioService action play add".logD()
                        getDefaultMediaItem { mediaList ->
                            exoPlayer.setMediaItems(mediaList)
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.prepare()
                                exoPlayer.play()
                            }
                            startForeground()
                        }
                    } else {
                        "AudioService action play".logD()
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                        startForeground()
                    }
                }

                ACTION_PREVIOUS -> {
                    "AudioService action previous".logD()
                    exoPlayer.seekToPrevious()
                    startForeground()
                }

                ACTION_NEXT -> {
                    "AudioService action next".logD()
                    exoPlayer.seekToNext()
                    startForeground()
                }
            }
        }
    }


    // 发出前台通知，widget发送操作通知情况
    @UnstableApi
    fun startForeground() {
        //                    startForeground(7,
//                        NotificationCompat.Builder(
//                            ContextManager.getContext(),
//                            "audio_channel_normal"
//                        )
//                            .setSmallIcon(com.dudu.common.R.mipmap.ic_launcher)
//                            .setColorized(true)// 是否允许设置背景颜色，可根据音乐数据进行切换
//                            .setContentTitle("test") // 通知标题
//                            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaLibrarySession))
//                            .build()
//
//                    )
        startForeground(
            AudioNotification.AUDIO_NOTIFICATION_ID,
            AudioNotification.getNotification(mediaLibrarySession)
        )
    }

    // 没Item情况，获取默认媒体数据，默认本地，否则添加一个线上数据
    private fun getDefaultMediaItem(block: (MutableList<MediaItem>) -> Unit) {
        audioServiceScope.launch {
            AudioLocalSource.query().flowOn(Dispatchers.IO)
                .collect {
                    if (it.size > 0) {
                        block(it)
                    } else {
                        block(
                            mutableListOf(
                                MediaItem.Builder().from(
                                    "audio_uri_1",
                                    "https://st.92kk.com//2022/前场包房/Hiphop/20220410/Brett_Young_In_Case_You_Didn_t_Know_[Plaid_Blazely_Edit].mp3",
                                    "mp3", "Uri_1", "Brett", duration = 240000
                                ).build()
                            )
                        )
                    }
                }
        }
    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onDestroy() {
        exoPlayer.release()
        mediaLibrarySession.release()
        audioServiceScope.cancel()
        super.onDestroy()
    }

    private fun sendUpdateWidgetReceive() {

        sendBroadcast(
            Intent("com.dudu.demo.update")
                .setPackage(packageName)
                .putExtra(RECEIVER_TITLE, exoPlayer.mediaMetadata.title)
                .putExtra(RECEIVER_PLAYING, exoPlayer.isPlaying)
        )
    }

}