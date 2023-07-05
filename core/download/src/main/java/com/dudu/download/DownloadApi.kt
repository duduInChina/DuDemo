package com.dudu.download

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 功能介绍
 * Created by Dzc on 2023/5/22.
 */
interface DownloadApi {

    @Streaming
    @GET
    suspend fun coroutinesDownload(@Url url: String, @HeaderMap headers: Map<String, String>): ResponseBody

}