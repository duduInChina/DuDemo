package com.dudu.audio

import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.dudu.common.ext.logD
import com.dudu.common.util.ContextManager
import kotlinx.coroutines.flow.flow

/**
 * 本地音乐音频数据源
 * Created by Dzc on 2023/8/31.
 */
object AudioLocalSource {

    private val PROJECTIONS = mutableListOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.MIME_TYPE
    ).also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            it.add(MediaStore.Audio.Media.GENRE)
        }
    }.toTypedArray()

    private const val SELECTION =
        "${MediaStore.Audio.Media.SIZE} >= ? and ${MediaStore.Audio.Media.DURATION} >= ? and ${MediaStore.Audio.Artists.ARTIST} != ?"

    private const val minSize = 0                // 音频大小
    private const val minDuration = 30 * 1000    // 查询的音频最少长度
    private const val unknownArt = "<unknown>"   // 演唱者不能为空
    private val SELECTION_ARGS = arrayOf("$minSize", "$minDuration", unknownArt)

    private const val SORT_ORDER = "${MediaStore.Audio.Media._ID} DESC"

    fun query() = flow<MutableList<MediaItem>> {

        val cursor = ContentResolverCompat.query(
            ContextManager.getContext().contentResolver,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            PROJECTIONS,
            SELECTION,
            SELECTION_ARGS,
            SORT_ORDER,
            null
        ) ?: throw IllegalArgumentException("暂无音频数据")

        cursor.logD()

        val list = mutableListOf<MediaItem>()

        do {
            val mediaItem = MediaItem.Builder()
                .from(cursor)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .from(cursor)
                        .build()
                ).build()
            list.add(mediaItem)
        } while (cursor.moveToNext())
        cursor.close()

        emit(list)
    }

}