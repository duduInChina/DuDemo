package com.dudu.album.util

import android.content.Intent
import android.provider.MediaStore

/**
 * 功能介绍
 * Created by Dzc on 2023/7/2.
 */
object AlbumUtil {

    /**
     * 打开系统相册
     */
    fun getSysAlbumIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        return intent
    }

}