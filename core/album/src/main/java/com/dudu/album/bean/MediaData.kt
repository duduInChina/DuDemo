package com.dudu.album.bean

import android.net.Uri
import android.os.Build
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/7/3.
 */
data class MediaData(
    var mediaId: Int,
    var parent: String,
    var name: String,
    var modifiedTime: Long,
) {

    private val BASE_VIDEO_URI = "content://media/external/video/media/"
    private val BASE_IMAGE_URI = "content://media/external/images/media/"

    fun getPath() = parent + name

    fun getUri(): Uri = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        Uri.fromFile(File(getPath()))
    } else {
        Uri.parse(BASE_IMAGE_URI + mediaId)
    }

}