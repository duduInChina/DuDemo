package com.dudu.audio

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaLibraryService
import com.dudu.common.executor.ExecutorManager
import com.dudu.common.ext.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * 音频操作类
 * Created by Dzc on 2023/8/30.
 */
object AudioManager {

    /////LiveData/////////
    // 当前播放列表
    val currentMediaList = MutableLiveData<MutableList<MediaItem>>(mutableListOf())
    // 当前播放的音频
    val currentMediaItem = MutableLiveData<MediaItem>()
    // 播放状态
    val isPlayingLiveData = MutableLiveData<Boolean>(false)
    // 当前播放进度
    val currentPosition = MutableLiveData<Long>(0L)
    val currentDuration = MutableLiveData<Long>(0L)
    // 进度轮询器
    private var positionHandler:Handler? = null
    /////////////////////

    private var audioScope:CoroutineScope? = null // 作用域

    private val audioServiceConnection by lazy {
        AudioServiceConnection { browser ->
            // 连接成功初始化
            audioScope = CoroutineScope(Dispatchers.Main)

            browser.repeatMode = Player.REPEAT_MODE_ALL // 全部重复播放模式
            browser.prepare()

            browser.currentMediaItem?.let {
                currentMediaItem.postValue(it)
            }
            isPlayingLiveData.postValue(browser.isPlaying)
            positionHandler = Handler(Looper.getMainLooper())
            updatePosition()

            browser.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    // 切换媒体
                    mediaItem?.let {
                        "onMediaItemTransition: ${mediaItem.mediaId}".logD()
                        currentMediaItem.postValue(it)

                        currentPosition.postValue(browser.currentPosition)
                    }

                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // 播放状态切换
                    "onIsPlayingChanged: $isPlaying".logD()
                    isPlayingLiveData.postValue(isPlaying)
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    //
                    "onTimelineChanged: ${timeline.toString()}".logD()
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    // 主动更变播放位置回调
                    "onPositionDiscontinuity $oldPosition $newPosition $reason".logD()
                }

                override fun onVolumeChanged(volume: Float) {
                    "onVolumeChanged $volume".logD()
                }

            })

            getBrowserMediaChildren()
        }
    }

    private val browser: MediaBrowser?
        get() = audioServiceConnection.browser

    /**
     * 关键启动连接，关键节点UI中onStart完成
     */
    fun connect() {
        audioServiceConnection.onStart()
    }

    /**
     * 释放连接资源
     */
    fun release() {
        audioServiceConnection.onStop()
        positionHandler?.removeCallbacks(this:: updatePosition)
        positionHandler?.removeCallbacksAndMessages(null)
        positionHandler = null
        audioScope?.cancel()
        audioScope = null
    }

    private fun updatePosition(){
        browser?.let {
            val cp = it.currentPosition
            val duration = it.duration
            if(currentDuration.value != duration){
                currentDuration.postValue(duration)
            }
            if(currentPosition.value != cp){
                currentPosition.postValue(cp)
            }
        }
        positionHandler?.postDelayed(this::updatePosition, 500)
    }

    /**
     * 时长
     */
    fun getDuration() : Long{
        return browser?.duration ?: 0
    }

    /**
     * 播放按钮状态切换
     */
    fun togglePlay(): Boolean{
        browser?.apply {
            if(isPlaying) pause() else play()
        }
        return browser?.isPlaying == true
    }

    /**
     * 上一首
     */
    fun previous(){
        browser?.seekToPrevious()
    }

    /**
     * 下一首
     */
    fun next(){
        browser?.seekToNext()
    }

    /**
     * 调整播放位置
     */
    fun seekTo(progress: Long) {
        browser?.seekTo(progress)
    }

    /**
     * 音量降低延时恢复，如播放提示音情况
     * TODO 声音可以逐渐递减，到逐渐递增
     */
    fun volumeDelayed(delayTime: Long){
        browser?.let {
            it.volume = 0.2f
            audioScope?.let { scope ->
                scope.launch {
                    delay(delayTime)
                    it.volume = 1f
                }
            }
        }
    }

    fun playByUri(uri: Uri) {
        browser?.apply {
            addMediaItem(currentMediaItemIndex, MediaItem.fromUri(uri))
            seekToDefaultPosition(currentMediaItemIndex)
            prepare()
            play()
        }
    }

    /**
     * 播放音频列表
     * startPositionMs开始播放位置
     */
    fun playMediaList(
        mediaItems: List<MediaItem>,
        startIndex: Int = 0,
        startPositionMs: Long = TIME_UNSET
    ) {
        // 删除当前播放列表相同项，播放列表置顶，重新加入
//        val playMediaId = mediaItems[startIndex].mediaId
//        var currentList = currentMediaList.value
//        val mediaItemIds = mediaItems.map { it.mediaId }
//        val filterList = currentList?.filter { it.mediaId !in mediaItemIds }
//        val dataList:MutableList<MediaItem> = mutableListOf()
//        dataList.addAll(mediaItems)
//        filterList?.let { dataList.addAll(it) }

        browser?.let {
            it.setMediaItems(mediaItems, startIndex, startPositionMs)
//            it.addMediaItems(0, mediaItems)
            it.seekToDefaultPosition(startIndex)
            it.prepare()
            it.play()
        }

//        currentMediaList.postValue(dataList)
    }

    fun playMediaItem(mediaItem: MediaItem) {
        val currentList = currentMediaList.value
        val filterList = currentList?.filter { it.mediaId == mediaItem.mediaId }
        if(!filterList.isNullOrEmpty()){
            browser?.apply {
                seekToDefaultPosition(currentList.indexOf(filterList[0]))
                prepare()
                play()
            }
        }else{
            browser?.apply {
                addMediaItem(0, mediaItem)
                seekToDefaultPosition(0)
                prepare()
                play()
            }
        }

    }

    @UnstableApi
    fun getBrowserMediaRoot() {
        val args = Bundle()
        args.putString("PACKAGE_NAME_KEY", PACKAGE_NAME)
        val params = MediaLibraryService.LibraryParams.Builder().setExtras(args).build()
        val libraryRoot = browser?.getLibraryRoot(params)
        libraryRoot?.addListener({
            val libraryResult = libraryRoot.get()

        }, ExecutorManager.executorFuture)
    }

    fun getBrowserMediaChildren() {
        // parentId 可以通过getLibraryRoot自定义规则获取
        // MoreExecutors.directExecutor 这个方法的主要用途之一是将异步代码以同步方式执行。当您需要确保任务立即在当前线程中执行，而不需要创建新线程或使用线程池时，可以使用 MoreExecutors.directExecutor()。
        val children = browser?.getChildren("1", 1, 10, null)
        children?.addListener({

            children.get()?.value?.let {
                currentMediaList.postValue(it)
            } ?: let {
                currentMediaList.postValue(mutableListOf())
            }

        }, ExecutorManager.executorFuture)
    }



}