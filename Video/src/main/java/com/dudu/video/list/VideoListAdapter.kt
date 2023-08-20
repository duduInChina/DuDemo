package com.dudu.video.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter
import com.dudu.video.databinding.ItemVideoBinding
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper

/**
 * 功能介绍
 * Created by Dzc on 2023/8/15.
 */
class VideoListAdapter(private val videoHelper: GSYVideoHelper): RecyclerAdapter<String, ItemVideoBinding>() {

    companion object {
        const val TAG = "VideoList"
    }

    override fun viewBinding(parent: ViewGroup): ItemVideoBinding {
        return ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemVideoBinding>,
        m: String,
        position: Int
    ) {

        holder.viewBanding {
            holder.tag ?: let {
                holder.tag = ImageView(playBtn.context)
            }

            videoHelper.addVideoPlayer(position, holder.tag as ImageView, TAG, contentLayout, playBtn)
            playBtn.tag = position
            playBtn.setOnClickListener { view ->
                val index = view.tag as Int
                videoHelper.setPlayPositionAndTag(index, TAG)
                this@VideoListAdapter.notifyDataSetChanged()

                videoHelper.gsyVideoOptionBuilder.setUrl(m)

                videoHelper.startPlay()
            }

        }

    }

}