package com.dudu.video.pager

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.util.StatusBarUtil
import com.dudu.video.VideoConstant
import com.dudu.video.databinding.ActivityVideoPagerBinding
import com.dudu.video.databinding.ItemVideoPagerBinding
import com.shuyu.gsyvideoplayer.GSYVideoManager

/**
 * 功能介绍
 * Created by Dzc on 2023/8/17.
 */
class VideoPagerActivity : BaseActivity<ActivityVideoPagerBinding>() {

    private val adapter: VideoPagerAdapter by lazy {
        VideoPagerAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtil.setStatusBarColor(window, ContextCompat.getColor(this, com.dudu.common.R.color.black))

        bodyBinding {
            viewPager.orientation = ORIENTATION_VERTICAL
            adapter.set(VideoConstant.urls)
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val playPosition = GSYVideoManager.instance().playPosition
                    if (playPosition >= 0 && GSYVideoManager.instance().playTag.equals(
                            VideoPagerAdapter.TAG
                        )
                    ) {
                        if (position != playPosition) {
                            GSYVideoManager.releaseAllVideos()
                            playPosition(position)
                        }
                    }

                }
            })

            viewPager.offscreenPageLimit = 3

            viewPager.post {
                playPosition(0)
            }
        }


    }

    fun playPosition(position: Int) {
        bodyBinding.viewPager.postDelayed({
            val viewHolder: CommonViewHolder<ItemVideoPagerBinding> =
                (bodyBinding.viewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(
                    position
                ) as CommonViewHolder<ItemVideoPagerBinding>

            viewHolder.viewBanding {
                videoPlayer.startPlayLogic()
            }

        }, 50)
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

}


