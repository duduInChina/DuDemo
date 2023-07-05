package com.dudu.weather.ui.weather

import androidx.lifecycle.MutableLiveData
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.ext.lifecycle
import com.dudu.weather.api.Repository
import com.dudu.weather.bean.Weather

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
class WeatherViewModel : BaseViewModel() {

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLoading = MutableLiveData(false)

    val weatherLiveData = MutableLiveData<Weather>()

    fun refreshWeather(lng: String, lat: String) {
        Repository.refresWeather(lng, lat).lifecycle(this, {
            weatherLoading.value = true
        }, {
            weatherLoading.value = false
        }, showFailedView = true) {
            weatherLoading.value = false

            weatherLiveData.value = this
        }
    }

}