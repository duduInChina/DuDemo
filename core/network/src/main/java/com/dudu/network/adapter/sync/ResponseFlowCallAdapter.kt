package com.dudu.network.adapter.sync

import com.dudu.common.exception.NetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ResponseFlowCallAdapter<R>(private val responseBodyType: R) :
    CallAdapter<R, Flow<Response<R>>> {
    override fun responseType() = responseBodyType as Type

    override fun adapt(call: Call<R>): Flow<Response<R>> = flow {
        suspendCancellableCoroutine<Response<R>> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                val response = call.execute()
                continuation.resume(response)
            } catch (e: Exception) {
                continuation.resumeWithException(NetworkException(e.message))
            }
        }.let {
            emit(it)
        }
    }.take(1)
}
