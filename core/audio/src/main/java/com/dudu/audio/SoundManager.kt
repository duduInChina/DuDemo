package com.dudu.audio

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION
import android.media.SoundPool
import androidx.annotation.RawRes
import com.dudu.common.util.ContextManager
import kotlin.math.max
import kotlin.math.min

/**
 * 提示音播放
 * 初始化需要预加载音频，不然第一次播放可能未完成
 * https://mp.weixin.qq.com/s/e4QJdp6S3ROQUrQ4jbfqiQ
 * Created by Dzc on 2023/9/5.
 */
class SoundManager(@RawRes resId: Int = 0) {

    private var soundPool: SoundPool? = null
    private var soundMap: HashMap<Int, Int> = hashMapOf()
    private var playMap: HashMap<Int, Int> = hashMapOf()

    init {
        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // 指定支持多少个声音，SoundPool对象中允许同时存在的最多的流的数量，该值太大就会报错
            .setAudioAttributes( // 音频播放的属性
                AudioAttributes.Builder()
                    .setUsage(USAGE_NOTIFICATION)
                    .setContentType(CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        if(resId != 0){
            load(resId)
        }
    }

    fun load(@RawRes resId: Int): Int? {
        return if (soundMap.containsKey(resId)) {
            soundMap[resId]
        } else {
            soundPool?.load(ContextManager.getContext(), resId, 1)?.let {
                soundMap.put(resId, it)
            }
            soundMap[resId]
        }
    }


    /**
     * 音频播放音量 range 0.0-1.0
     */
    var soundPlayVolume: Float = 1f
        set(value) {
            field = min(1f, max(value, 0f))
        }

    /**
     * 播放
     */
    fun play(@RawRes resId: Int) {
        load(resId)?.let { loadId ->
            soundPool?.let {
                val playId = it.play(
                    loadId,          // 声音id
                    soundPlayVolume, // 左右声道音量
                    soundPlayVolume,
                    1,       // 播放优先级
                    0,             // 循环模式：0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次
                    1f             // 播放速度：1是正常，范围从0~2
                )
                playMap.put(resId, playId)
            }
        }
    }

    /**
     * 暂停播放
     */
    fun pause(@RawRes resId: Int){
        if(playMap.containsKey(resId)){
            soundPool?.pause(playMap[resId]!!)
        }
    }

    /**
     * 继续播放
     */
    fun resume(@RawRes resId: Int){
        if(playMap.containsKey(resId)){
            soundPool?.resume(playMap[resId]!!)
        }
    }

    /**
     * 释放
     */
    fun release(){
        soundPool?.autoPause()
        soundPool?.release()
    }

}