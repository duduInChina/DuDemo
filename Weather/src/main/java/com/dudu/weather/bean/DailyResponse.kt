package com.dudu.weather.bean

import com.dudu.network.bean.IResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
@Serializable
data class DailyResponse(val result: Result, val status: String, val error: String = "") : IResult {

    @Serializable
    data class Result(val daily: Daily)

    @Serializable
    data class Daily(
        val temperature: List<Temperature>,
        val skycon: List<Skycon>,
        @SerialName("life_index") val lifeIndex: LiseIndex
    )

    @Serializable
    data class Temperature(val max: Float, val min: Float)

    @Serializable
    data class Skycon(val value: String, val date: String)

    @Serializable
    data class LiseIndex(
        val coldRisk: List<LifeDescription>,
        val carWashing: List<LifeDescription>,
        val ultraviolet: List<LifeDescription>,
        val dressing: List<LifeDescription>
    )

    @Serializable
    data class LifeDescription(val desc: String)

    override fun getErrorCode() = status

    override fun isSuccess() = "ok"

    override fun getErrorMessage() = error

}