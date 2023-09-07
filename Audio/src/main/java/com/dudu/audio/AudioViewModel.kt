package com.dudu.audio

import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.ext.lifecycle
import com.dudu.common.ext.logD

/**
 * 功能介绍
 * Created by Dzc on 2023/8/31.
 */
class AudioViewModel : BaseViewModel() {

    val localAudioDataLiveData = MutableLiveData<MutableList<MediaItem>>()

    val liveAudioDataLiveData = MutableLiveData<MutableList<MediaItem>>()

    fun audioLoad() {
        AudioLocalSource.query().lifecycle(this, errorCallback = {
            "error $it".logD()
        }) {
            localAudioDataLiveData.value = this
        }
    }

    fun liveLoad() {
        val data = mutableListOf(
            MediaItem.Builder().from("audio_uri_1",
                "https://st.92kk.com//2022/前场包房/Hiphop/20220410/Brett_Young_In_Case_You_Didn_t_Know_[Plaid_Blazely_Edit].mp3",
                "mp3", "Uri_1", "Brett",duration = 240000)
                .build(),
            MediaItem.Builder().from("audio_uri_2",
                "https://st.92kk.com//2016/前场包房/前场Deep/20161218/128_Tech_House_Housefly_Happy_[Radio_Edit].mp3",
                "mp3", "Uri_2","Tech", duration = 215000)
                .build(),
            MediaItem.Builder().from("audio_uri_3",
                "https://st.92kk.com//2022/前场包房/Dubstep/20220410/Princess_Nokia_x_Blanke_I_Like_Him_[TANE_Edit].mp3",
                "mp3", "Uri_3","Princess", duration = 130000)
                .build(),
        )
        liveAudioDataLiveData.value = data
    }


}