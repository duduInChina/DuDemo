package com.dudu.audio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import com.dudu.common.base.adapter.CommonViewHolder
import com.dudu.common.base.adapter.RecyclerAdapter
import com.dudu.demoaudio.databinding.ItemAudioBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.toDuration

/**
 * 功能介绍
 * Created by Dzc on 2023/8/31.
 */
class AudioAdapter : RecyclerAdapter<MediaItem, ItemAudioBinding>() {
    // 当前播放id
    var currentMediaId = ""

    override fun viewBinding(parent: ViewGroup): ItemAudioBinding {
        return ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindViewHolder(
        holder: CommonViewHolder<ItemAudioBinding>,
        m: MediaItem,
        position: Int
    ) {
        holder.viewBanding {
            title.text = m.mediaMetadata.title
//            val sub = "${m.mediaMetadata.artist} - ${m.mediaMetadata.albumTitle}"
            subtitle.text = m.mediaMetadata.artist
//            val type =
//                m.localConfiguration?.mimeType?.split("/")?.toTypedArray()?.last()?.uppercase()

            val duration = SimpleDateFormat(
                if (m.mediaMetadata.getDuration() > 1000 * 60 * 60) "H:mm:ss" else "mm:ss",
                Locale.CHINA
            ).format(
                Date(m.mediaMetadata.getDuration())
            )
//            val td = "$type - $duration"
            time.text = duration

            content.setOnClickListener {
                AudioManager.playMediaItem(m)
            }

            if(currentMediaId == m.mediaId){
                status.visibility = View.VISIBLE
            }else{
                status.visibility = View.GONE
            }

        }
    }
}