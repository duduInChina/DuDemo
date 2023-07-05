package com.dudu.weather.api

import com.dudu.weather.WeatherConstant
import com.dudu.weather.bean.DailyResponse
import com.dudu.weather.bean.RealtimeResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
interface WeatherService {

    @GET("v2.5/${WeatherConstant.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Flow<RealtimeResponse>

    @GET("v2.5/${WeatherConstant.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Flow<DailyResponse>

}