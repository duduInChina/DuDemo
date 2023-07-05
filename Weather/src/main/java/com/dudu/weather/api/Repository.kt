package com.dudu.weather.api

import com.dudu.network.RetrofitManager
import com.dudu.weather.WeatherConstant
import com.dudu.weather.bean.PlaceResponse
import com.dudu.weather.bean.Weather
import com.dudu.weather.dao.WeatherDao
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip

/**
 * 功能介绍
 * Created by Dzc on 2023/5/11.
 */
object Repository {

    val placeService by lazy {
        RetrofitManager.createService(WeatherPlaceApi::class.java, WeatherConstant.BASEURL)
    }

    val weatherService by lazy {
        RetrofitManager.createService(WeatherService::class.java, WeatherConstant.BASEURL)
    }

//    fun searchPlaces(query: String) = flow {
//        emit(placeService.searchPlace(query))
//    }

    // 组合请求 combine 短的请求等待长请求，zip 短请求结束就结束
    fun refresWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).combine(
        weatherService.getDailyWeather(lng, lat)
    ) { realtimeResponse, dailyResponse ->
        Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
    }


    fun savePlace(place: PlaceResponse.Place) = flow {
        emit(WeatherDao.savePlace(place))
    }

    fun getSavePlace() = flow {
        emit(WeatherDao.getSavedPlace())
    }

}