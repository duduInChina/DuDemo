package com.dudu.imageloader

import android.widget.ImageView
import coil.load
import com.dudu.common.base.view.ImageLoader

/**
 * 功能介绍
 * Created by Dzc on 2023/7/2.
 */
object CoilImageLoader: ImageLoader {
    override fun load(imageView: ImageView, data: Any?) {
        imageView.load(data)
    }
}