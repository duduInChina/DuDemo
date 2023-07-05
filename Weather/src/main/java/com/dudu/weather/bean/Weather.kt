package com.dudu.weather.bean

/**
 * 功能介绍
 * Created by Dzc on 2023/5/16.
 */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
