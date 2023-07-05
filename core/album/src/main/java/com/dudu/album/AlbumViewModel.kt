package com.dudu.album

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dudu.album.bean.MediaData
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.ext.lifecycle

/**
 * 功能介绍
 * Created by Dzc on 2023/7/3.
 */
class AlbumViewModel : BaseViewModel() {

    val mediaDataLiveData = MutableLiveData<MutableList<MediaData>>()

    fun mediaLoad() {
        MediaLoader.query().lifecycle(this){
            mediaDataLiveData.value = this
        }
    }
}