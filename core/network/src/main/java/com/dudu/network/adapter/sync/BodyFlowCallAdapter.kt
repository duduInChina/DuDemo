package com.dudu.network.adapter.sync

import com.dudu.common.exception.HttpException
import com.dudu.common.exception.NetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 功能介绍
 * Created by Dzc on 2023/5/12.
 */
class BodyFlowCallAdapter<R>(private val responseBodyType: R) : CallAdapter<R, Flow<R>> {
    override fun responseType() = responseBodyType as Type

    override fun adapt(call: Call<R>): Flow<R> = flow {
        suspendCancellableCoroutine<R> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    continuation.resume(response.body()!!)
                } else {
                    // 服务器问题
                    continuation.resumeWithException(HttpException(response.code(), response.message()))
                }
            } catch (e: Exception) {
                // 网络问题
                continuation.resumeWithException(NetworkException(e.message))
            }
        }.let {
            emit(it)
        }
    }.take(1)
}