package com.dudu.common.util

import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 功能介绍
 * Created by Dzc on 2023/9/1.
 */
object JsonUtil {
    val json by lazy {
        Json {
            ignoreUnknownKeys =
                true // 默认情况下，json和实例化对像键值需要一一对应，但实际案例中，json和对象参数名可能无法一一对应，这种情况可以忽略未使用的键
            coerceInputValues = true // json的值类型和实例化对象类型不一致时(如json字段值为null)，使用对象的默认值
            isLenient = true // 宽松解析，json中可能为String的可解析为Int
        }
    }

    inline fun <reified T>  encodeToString(value: T) = json.encodeToString(value)

    inline fun <reified T> decodeFromString(string: String): T = json.decodeFromString(string)
}