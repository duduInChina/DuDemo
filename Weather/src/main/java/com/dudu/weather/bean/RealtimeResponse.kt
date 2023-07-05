package com.dudu.weather.bean

import com.dudu.network.bean.IResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
@Serializable
data class RealtimeResponse(val result: Result, val status: String, val error: String = "") :
    IResult {

    @Serializable
    data class Result(val realtime: Realtime)

    @Serializable
    data class Realtime(
        val skycon: String,
        val temperature: Float,
        @SerialName("air_quality") val airQuality: AirQuality
    )

    @Serializable
    data class AirQuality(val aqi: AQI)

    @Serializable
    data class AQI(val chn: Float)

    override fun getErrorCode() = status

    override fun isSuccess() = "ok"

    override fun getErrorMessage() = error
}