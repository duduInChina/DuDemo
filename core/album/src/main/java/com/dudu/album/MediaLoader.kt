package com.dudu.album

import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import com.dudu.album.bean.MediaData
import com.dudu.common.util.ContextManager
import com.dudu.common.util.FileUtil
import kotlinx.coroutines.flow.flow


/**
 * 功能介绍
 * Created by Dzc on 2023/7/3.
 */
object MediaLoader {
    private val CONTENT_URI = MediaStore.Files.getContentUri("external")
    private const val IDX_ID = 0
    private const val IDX_DATA = 1
    private const val IDX_MEDIA_TYPE = 2
    private const val IDX_DATE_MODIFIED = 3
    private const val IDX_MIME_TYPE = 4
    private const val IDX_DURATION = 5
    private const val IDX_SIZE = 6
    private const val IDX_WIDTH = 7
    private const val IDX_HEIGHT = 8
    private const val IDX_ORIENTATION = 9
    private val PROJECTIONS = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.MediaColumns.DATE_MODIFIED,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.Video.Media.DURATION,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.Images.Media.ORIENTATION
    )
    private const val TYPE_SELECTION = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
            + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + ")")

    /**
     * 获得读READ_EXTERNAL_STORAGE权限才会，包含其他应用的路径返回
     */
    fun query() = flow<MutableList<MediaData>> {
        val cursor = ContentResolverCompat.query(
            ContextManager.getContext().contentResolver,
            CONTENT_URI, PROJECTIONS, TYPE_SELECTION, null, null, null
        ) ?: throw IllegalArgumentException("暂无图片数据")

        val list = mutableListOf<MediaData>()

        try {

            while (cursor.moveToNext()){
                val id = cursor.getInt(IDX_ID)
                val path = cursor.getString(IDX_DATA)

                if(path == null || path.isEmpty()) continue

                val parent = FileUtil.getParentPath(path)
                val name = FileUtil.getFileName(path)
                val time = cursor.getLong(IDX_DATE_MODIFIED)

                val type = cursor.getInt(IDX_MEDIA_TYPE)

                if(type != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                    val item = MediaData(id, parent, name, time)
                    list.add(item)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }

        emit(list)
    }

}














