package com.dudu.video

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dudu.common.util.ContextManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer


/**
 * 功能介绍
 * Created by Dzc on 2023/8/20.
 */
class FloatPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        val floatWindow by lazy {
            FloatWindow()
        }

        fun show() {
            if (!floatWindow.isShow()) {
                floatWindow.init(FloatPlayerView(ContextManager.getContext()))
            }
        }

        fun dismiss(){
            if(floatWindow.isShow()){
                val floatPlayerView = floatWindow.mView as FloatPlayerView
//                floatPlayerView.destroy()
                floatWindow.dismiss()
            }

        }
    }

    val videoPlayer: StandardGSYVideoPlayer by lazy {
        val videoPlayer = StandardGSYVideoPlayer(context)

        val layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        addView(videoPlayer, layoutParams)

        videoPlayer.setIsTouchWiget(false)

        videoPlayer
    }

    init {
//        setBackgroundColor(context.getColor(com.dudu.common.R.color.blue))
//        StandardGSYVideoPlayer()
        videoPlayer.setUp("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", true, "")

    }

//    fun destroy(){
//        videoPlayer?.currentPlayer?.let {
//            it.release()
//        }
//    }



}