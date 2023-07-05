package com.dudu.weather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.exception.ExceptionManager
import com.dudu.common.ext.lifecycle
import com.dudu.weather.api.Repository
import com.dudu.weather.bean.PlaceResponse

/**
 * 功能介绍
 * Created by Dzc on 2023/5/11.
 */
class PlaceViewModel : BaseViewModel() {

    val placeLoading = MutableLiveData(false)

    val placeLiveData = MutableLiveData<MutableList<PlaceResponse.Place>>()

    fun searchPlaces(query: String, delayTime: Long = 0) {
        Repository.placeService.searchPlace(query).lifecycle(this, {
            placeLoading.value = true
        }, {
            placeLoading.value = false
            var showFailedView = true
            placeLiveData.value?.let { placeLiveData ->
                if(placeLiveData.size > 0){
                    showFailedView = false
                }
            }

            ExceptionManager.failedLogic(this, it, showFailedView)
        }, delayTime = delayTime) {
            placeLoading.value = false
            placeLiveData.value = this.places
        }
    }

    fun getSavedPlace() = Repository.getSavePlace().asLiveData()

    fun savePlace(place: PlaceResponse.Place) = Repository.savePlace(place).asLiveData()

}