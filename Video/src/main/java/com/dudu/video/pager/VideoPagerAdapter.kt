package com.dudu.video.pager

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter
import com.dudu.video.databinding.ItemVideoBinding
import com.dudu.video.databinding.ItemVideoPagerBinding
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder

/**
 * 功能介绍
 * Created by Dzc on 2023/8/17.
 */
class VideoPagerAdapter : RecyclerAdapter<String, ItemVideoPagerBinding>() {

    companion object {
        const val TAG = "VideoPager"
    }

    override fun viewBinding(parent: ViewGroup): ItemVideoPagerBinding {
        return ItemVideoPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemVideoPagerBinding>,
        m: String,
        position: Int
    ) {
        holder.tag ?: let {
            holder.tag = GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setCacheWithPlay(true)
                .setRotateViewAuto(false)
                .setPlayTag(TAG)
        }

        holder.viewBanding {
            val builder = holder.tag as GSYVideoOptionBuilder
            builder.setPlayPosition(position)
                .setUrl(m)
                .build(videoPlayer)

            videoPlayer.backButton.visibility = GONE
        }

    }
}