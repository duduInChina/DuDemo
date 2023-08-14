package com.dudu.upload

import com.dudu.network.bean.ResultData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Url

/**
 * 功能介绍
 * Created by Dzc on 2023/8/6.
 */
interface UploadApi {

    @Multipart
    @POST
    fun uploadFile(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): Flow<ResultData<String>>

}