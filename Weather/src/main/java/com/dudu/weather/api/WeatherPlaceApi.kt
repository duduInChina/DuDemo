package com.dudu.weather.api

import com.dudu.weather.WeatherConstant
import com.dudu.weather.bean.PlaceResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit 从 2.6.4 开始只支持 suspend 关键字，Retrofit内部 会自动为suspend方法创建CallAdapter
 * 使用Flow会自动完成取消操作
 * Created by Dzc on 2023/5/8.
 */
interface WeatherPlaceApi {
    @GET("v2/place?token=${WeatherConstant.TOKEN}&lang=zh_CN")
    fun searchPlace(@Query("query") query: String): Flow<PlaceResponse>
}