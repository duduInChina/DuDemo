package com.dudu.network.adapter

import com.dudu.network.adapter.async.AsyncBodyFlowCallAdapter
import com.dudu.network.adapter.async.AsyncResponseFlowCallAdapter
import com.dudu.network.adapter.sync.BodyFlowCallAdapter
import com.dudu.network.adapter.sync.ResponseFlowCallAdapter
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 返回Flow的adapter,参考 sharp-retrofit库
 * Created by Dzc on 2023/5/12.
 */
class FlowCallAdapterFactory(private val async: Boolean) : CallAdapter.Factory() {

    companion object {
        fun create(async: Boolean = false) = FlowCallAdapterFactory(async)
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        if (getRawType(returnType) != Flow::class.java) return null

        if (returnType !is ParameterizedType) {
            throw IllegalStateException("the flow type must be parameterized as Flow<Foo>!")
        }

        val flowableType = getParameterUpperBound(0, returnType)
        val rawFlowableType = getRawType(flowableType)

        return if (rawFlowableType == Response::class.java) {
            if (flowableType !is ParameterizedType) {
                throw IllegalStateException("the response type must be parameterized as Response<Foo>!")
            }
            val responseBodyType = getParameterUpperBound(0, flowableType)
            createResponseFlowCallAdapter(async, responseBodyType)
        } else {
            createBodyFlowCallAdapter(async, flowableType)
        }
    }

    private fun createBodyFlowCallAdapter(async: Boolean, responseBodyType: Type) =
        if (async)
            AsyncBodyFlowCallAdapter(responseBodyType)
        else
            BodyFlowCallAdapter(responseBodyType)

    private fun createResponseFlowCallAdapter(async: Boolean, responseBodyType: Type) =
        if (async)
            AsyncResponseFlowCallAdapter(responseBodyType)
        else
            ResponseFlowCallAdapter(responseBodyType)

}