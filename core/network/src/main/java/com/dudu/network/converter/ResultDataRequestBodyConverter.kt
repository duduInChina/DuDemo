package com.dudu.network.converter

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets.UTF_8

/**
 * 功能介绍
 * Created by Dzc on 2023/5/10.
 */
class ResultDataRequestBodyConverter(private val type: Type) : Converter<Any, RequestBody> {

    private var json: Json = Json

    companion object{
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
    }

    override fun convert(value: Any): RequestBody? {
        val requestBodyString = json.encodeToString(serializer(type), value)
        val buffer = Buffer()
        buffer.writeString(requestBodyString, UTF_8)
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }
}