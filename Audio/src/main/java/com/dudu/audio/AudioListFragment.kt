package com.dudu.audio

import androidx.recyclerview.widget.LinearLayoutManager
import com.dudu.common.base.fragment.BaseVMFragment
import com.dudu.demoaudio.databinding.FragmentAudioListBinding

/**
 * 功能介绍
 * Created by Dzc on 2023/8/31.
 */

sealed class AudioListType{
    // 本地音乐列表
    object Local : AudioListType()
    // 正在播放列表
    object Current : AudioListType()
    // 线上播放列表
    object Url : AudioListType()
}

class AudioLocalListFragment(private val type: AudioListType):BaseVMFragment<FragmentAudioListBinding, AudioViewModel>() {

    private lateinit var adapter: AudioAdapter

    override fun initView() {

        bodyBinding {
            val layoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = layoutManager
            adapter = AudioAdapter()
            adapter.currentMediaId = AudioManager.currentMediaItem.value?.mediaId ?: ""
            recyclerView.adapter = adapter

        }

        when (type){
            is AudioListType.Local -> {
                adapter.set(viewModel.localAudioDataLiveData.value)
                viewModel.audioLoad()
            }
            is AudioListType.Current -> {
                adapter.set(AudioManager.currentMediaList.value)
                AudioManager.getBrowserMediaChildren()
            }
            is AudioListType.Url -> {
                viewModel.liveLoad()
            }
        }


    }

    override fun initFlow() {

        when (type){
            is AudioListType.Local -> {
                viewModel.localAudioDataLiveData.observe(this){ audioData ->
                    adapter.set(audioData)
                }
            }
            is AudioListType.Current -> {
                AudioManager.currentMediaList.observe(this){ audioData ->
                    adapter.set(audioData)
                }
            }
            is AudioListType.Url -> {
                viewModel.liveAudioDataLiveData.observe(this){ audioData ->
                    adapter.set(audioData)
                }
            }
        }

        // 当前媒体
        AudioManager.currentMediaItem.observe(this){ mediaItem ->
            adapter.currentMediaId = mediaItem.mediaId
            adapter.notifyDataSetChanged()
        }

    }

    override fun onStart() {
        super.onStart()
        AudioManager.connect()
    }
}