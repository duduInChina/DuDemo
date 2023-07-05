package com.dudu.network.converter

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 功能介绍
 * Created by Dzc on 2023/5/10.
 */
class ResultDataConverterFactory() : Converter.Factory() {

    companion object {
        fun create(): ResultDataConverterFactory {
            return ResultDataConverterFactory()
        }
    }


    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return ResultDataRequestBodyConverter(type)
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return ResultDataResponseBodyConverter(type)
    }

}