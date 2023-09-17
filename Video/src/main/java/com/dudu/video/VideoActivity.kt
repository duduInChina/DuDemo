package com.dudu.video

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.dudu.common.ext.logD
import com.dudu.common.ext.registerResult
import com.dudu.common.router.RouterPath
import com.dudu.common.util.ContextManager
import com.dudu.common.util.StatusBarUtil
import com.dudu.video.databinding.ActivityVideoBinding
import com.dudu.video.list.VideoListActivity
import com.dudu.video.pager.VideoPagerActivity
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.therouter.router.Route
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.Danmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

/**
 * 功能介绍
 * Created by Dzc on 2023/8/14.
 */
@Route(path = RouterPath.VIDEO)
class VideoActivity :
    VideoDetailBaseActivity<ActivityVideoBinding, VideoViewModel, StandardGSYVideoPlayer>() {

    private val url = "http://alvideo.ippzone.com/zyvd/98/90/b753-55fe-11e9-b0d8-00163e0c0248"

    var index = 0;

    val danmakuContext by lazy {
        DanmakuContext.create()
    }

    // 获取浮窗权限回调
    val floatWinResult = registerResult { _, _ ->
        if (Settings.canDrawOverlays(ContextManager.getContext())) {
            // 弹出浮窗
        } else {
            ContextManager.showToast("未获取到浮窗权限")
        }
    }

//    val floatWindow by lazy {
//        FloatWindow()
//    }

    override fun initParam() {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        StatusBarUtil.setStatusBarColor(
            window,
            ContextCompat.getColor(this, com.dudu.common.R.color.black)
        )
    }

    override fun initView() {

        bodyBinding {
            danmakuSend.setOnClickListener {
                danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)?.let {
                    it.text = "这是一条弹幕" + index++
//                    it.padding = 5
                    it.priority = 8 // 可选，设置弹幕优先级
                    it.textColor = Color.RED
                    it.textSize = 30f
                    it.isLive = true
                    danmakuView.addDanmaku(it)
                    "111".logD()
//                    danmakuView.invalidateDanmaku(it, false)
                }
            }
            danmakuView.prepare(object : BaseDanmakuParser() {
                override fun parse(): Danmakus {
                    // 这里解析弹幕数据并返回一个 Danmaku 对象
                    return Danmakus()
                }
            }, danmakuContext)
            danmakuView.setCallback(object : DrawHandler.Callback {
                override fun prepared() {
                    danmakuView.start()
                    "222".logD()
                }

                override fun updateTimer(timer: DanmakuTimer?) {
                }

                override fun danmakuShown(danmaku: BaseDanmaku?) {
                }

                override fun drawingFinished() {
                }
            })

            videoList.setOnClickListener {
                startActivity(Intent(this@VideoActivity, VideoListActivity::class.java))
            }
            videoPage.setOnClickListener {
                startActivity(Intent(this@VideoActivity, VideoPagerActivity::class.java))
            }

            if(FloatPlayerView.floatWindow.isShow()){
                floatWinText.text = "关闭视频窗口"
            }

            floatWin.setOnClickListener {
                if (Settings.canDrawOverlays(ContextManager.getContext())) {
                    // 弹出浮窗
                    if(FloatPlayerView.floatWindow.isShow()){
                        // 关闭
                        FloatPlayerView.dismiss()
                        floatWinText.text = "打开视频窗口"
                    }else{
                        // 打开
                        FloatPlayerView.show()
                        floatWinText.text = "关闭视频窗口"
                    }

                } else {
                    floatWinResult.launch(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
                }
            }

        }

        initVideo()
    }

    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder =
        GSYVideoOptionBuilder()
            .setUrl(url)
            .setCacheWithPlay(true) // 缓存
            .setIsTouchWiget(true) // 滑动界面改变进度，声音等默认true
            .setRotateViewAuto(false) // 是否开启自动旋转
            .setLockLand(false) // 一全屏就锁屏横屏，默认false竖屏，可配合setRotateViewAuto使用
            .setShowFullAnimation(false) // 全屏动画
            .setNeedLockFull(true) // 需要全屏锁定屏幕功能如果单独使用请设置setIfCurrentIsFullscreen为true
            .setSeekRatio(1f) // 滑动快进的比例，默认1。数值越大，滑动的产生的seek越小

    override fun initFlow() {

    }

    override fun getGSYVideoPlayer(): StandardGSYVideoPlayer = bodyBinding.videoPlayer


    override fun getDetailOrientationRotateAuto(): Boolean = true


    override fun onPause() {
        super.onPause()

        if (bodyBinding.danmakuView.isPrepared) {
            bodyBinding.danmakuView.pause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (bodyBinding.danmakuView.isPrepared && bodyBinding.danmakuView.isPaused) {
            bodyBinding.danmakuView.resume()
        }
    }

}