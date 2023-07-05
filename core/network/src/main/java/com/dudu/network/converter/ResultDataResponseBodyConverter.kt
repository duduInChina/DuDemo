package com.dudu.network.converter

import com.dudu.network.bean.IResult
import com.dudu.common.exception.JsonException
import com.dudu.common.exception.ResultException
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * 功能介绍
 * Created by Dzc on 2023/5/10.
 */
class ResultDataResponseBodyConverter(private val type: Type) : Converter<ResponseBody, Any> {

    private var json: Json = Json{
        // 相关参数解析 https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#json-configuration
        ignoreUnknownKeys = true // 默认情况下，json和实例化对像键值需要一一对应，但实际案例中，json和对象参数名可能无法一一对应，这种情况可以忽略未使用的键
        coerceInputValues = true // json的值类型和实例化对象类型不一致时(如json字段值为null)，使用对象的默认值
        isLenient = true // 宽松解析，json中可能为String的可解析为Int
    }

    override fun convert(value: ResponseBody): Any? {

        val valueString = value.string()
        val clazz = TypeToken.get(type).rawType

        return if (IResult::class.java.isAssignableFrom(clazz)) {

            val result: IResult = try {
                json.decodeFromString(serializer(type), valueString) as IResult
            } catch (e: Exception){
                e.printStackTrace()
                throw JsonException(e.message)
            }
            when (result.getErrorCode()) {
                result.isSuccess() -> result
                else -> throw ResultException(result.getErrorCode(), result.getErrorMessage())
            }

        } else {
            try {
                json.decodeFromString(serializer(type), valueString)
            } catch (e: Exception){
                e.printStackTrace()
                throw JsonException(e.message)
            }
        }
    }
}