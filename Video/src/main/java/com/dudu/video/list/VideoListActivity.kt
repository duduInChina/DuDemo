package com.dudu.video.list

import android.os.Bundle
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.annotation.Title
import com.dudu.video.VideoConstant
import com.dudu.video.databinding.ActivityVideoListBinding
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper.GSYVideoHelperBuilder
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager


/**
 * 功能介绍
 * Created by Dzc on 2023/8/15.
 */
@Title("视频列表")
class VideoListActivity : BaseActivity<ActivityVideoListBinding>() {

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this@VideoListActivity)
    }

    private val adapter: VideoListAdapter by lazy {
        VideoListAdapter(videoHelper)
    }

    private val videoHelper: GSYVideoHelper by lazy {
        val vh = GSYVideoHelper(this, NormalGSYVideoPlayer(this))
//        vh.setFullViewContainer()
        vh
    }

    private val videoHelperBuilder: GSYVideoHelperBuilder by lazy {
        val builder = GSYVideoHelperBuilder()
        builder.setHideActionBar(true)
            .setHideStatusBar(true)
            .setCacheWithPlay(true)
        builder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        bodyBinding {
            recyclerView.layoutManager = layoutManager
            adapter.set(VideoConstant.urls)
            recyclerView.adapter = adapter
        }

//        videoHelper.setFullViewContainer()
        videoHelper.setGsyVideoOptionBuilder(videoHelperBuilder)

        bodyBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if(videoHelper.playPosition >= 0 && videoHelper.playTAG.equals(VideoListAdapter.TAG)){
                    val position = videoHelper.playPosition
                    if(position < firstPosition || position > lastPosition){
                        // 超出界面，暂停
                        if(!videoHelper.isFull && !videoHelper.isSmall){
                            videoHelper.releaseVideoPlayer()
                            GSYVideoManager.releaseAllVideos()
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        onBackPressedDispatcher.addCallback(this) {
            if (!videoHelper.backFromFull()) {
                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        videoHelper.releaseVideoPlayer()
        GSYVideoManager.releaseAllVideos()
    }


}