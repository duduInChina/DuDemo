package com.dudu.album

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dudu.album.bean.MediaData
import com.dudu.album.databinding.ItemAlbumBinding
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter

/**
 * 功能介绍
 * Created by Dzc on 2023/7/3.
 */
class AlbumAdapter : RecyclerAdapter<MediaData, ItemAlbumBinding>() {
    override fun viewBinding(parent: ViewGroup): ItemAlbumBinding {
        return ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemAlbumBinding>,
        m: MediaData,
        position: Int
    ) {
        holder.viewBanding {
            AlbumConstant.imageLoader?.load(albumImage, m.getUri())
        }

    }
}