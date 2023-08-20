package com.dudu.video

import android.content.res.Configuration
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.viewbinding.ViewBinding
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.base.viewmodel.BaseViewModel
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer


/**
 * 功能介绍
 * Created by Dzc on 2023/8/14.
 */
abstract class VideoDetailBaseActivity<VB : ViewBinding, VM : BaseViewModel, VP : GSYBaseVideoPlayer> :
    BaseVMActivity<VB, VM>(), VideoAllCallBack {

    // 外部辅助的旋转，帮助全屏
    val orientationUtils: OrientationUtils by lazy {
        OrientationUtils(this, getGSYVideoPlayer())
    }

    fun initVideo() {
        // 初始化不打开外部的选择
        orientationUtils.isEnable = false

        getGSYVideoOptionBuilder()
            .setVideoAllCallBack(this)
            .build(getGSYVideoPlayer())

        getGSYVideoPlayer().fullscreenButton?.let {
            getGSYVideoPlayer().fullscreenButton.setOnClickListener {
                showFull()
            }
        }

        // 返回监听
        onBackPressedDispatcher.addCallback(this) {
            orientationUtils.backToProtVideo()
            if (!GSYVideoManager.backFromWindowFull(this@VideoDetailBaseActivity)) {
                finish()
            }
        }

    }

    // 全屏
    fun showFull() {
        if (orientationUtils.isLand != 1) {
            // 直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils.resolveByClick()
        }

        getGSYVideoPlayer().startWindowFullscreen(
            this,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull()
        )
    }

    abstract fun getGSYVideoPlayer(): VP

    abstract fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder

    fun hideActionBarWhenFull() = true

    fun hideStatusBarWhenFull() = true

    /**
     * 是否启用旋转横屏
     */
    abstract fun getDetailOrientationRotateAuto(): Boolean

    ////////////  生命周期  ////////////
    var isPlay = false
    var isPause = false

    override fun onResume() {
        super.onResume()
        getGSYVideoPlayer().currentPlayer.onVideoResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        getGSYVideoPlayer().currentPlayer.onVideoPause()
        orientationUtils.setIsPause(true)
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            getGSYVideoPlayer().currentPlayer.release()
        }
        orientationUtils.releaseListener()
    }

    /**
     * 监听旋转
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            getGSYVideoPlayer().onConfigurationChanged(
                this,
                newConfig,
                orientationUtils,
                hideActionBarWhenFull(),
                hideStatusBarWhenFull()
            )
        }
    }

    //////////// setVideoAllCallBack,播放状态 //////////////////
    override fun onStartPrepared(url: String?, vararg objects: Any?) {
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        // 开始播放才旋转和全屏
        orientationUtils.isEnable = getDetailOrientationRotateAuto()
        isPlay = true
    }

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {
    }

    override fun onClickStartError(url: String?, vararg objects: Any?) {
    }

    override fun onClickStop(url: String?, vararg objects: Any?) {
    }

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickResume(url: String?, vararg objects: Any?) {
    }

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {
    }

    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onAutoComplete(url: String?, vararg objects: Any?) {
    }

    override fun onComplete(url: String?, vararg objects: Any?) {
    }

    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        // 退出全屏
        orientationUtils.backToProtVideo()
    }

    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {
    }

    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {
    }

    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {
    }

    override fun onPlayError(url: String?, vararg objects: Any?) {
    }

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {
    }

    override fun onClickBlank(url: String?, vararg objects: Any?) {
    }

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {
    }
    //////////////////////////////
}