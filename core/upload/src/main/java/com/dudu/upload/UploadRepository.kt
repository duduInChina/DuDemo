package com.dudu.upload

import com.dudu.network.RetrofitManager
import com.dudu.network.bean.ResultData
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/8/7.
 */
object UploadRepository {

    // 由于下载的baseurl未必是一个确定值，所以根据下载链接再次切换调整
    private const val BASEURL = "https://example.com"

    private val uploadService by lazy {
        RetrofitManager.createService(UploadApi::class.java, BASEURL)
    }

    fun uploadFile(
        uploadUrl: String,
        file: File,
        params: Map<String, String>?
    ): Flow<ResultData<String>> {
        if (!file.isFile) {
            throw IllegalArgumentException("上传非文件类型")
        }

        val fileRequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

        val requestBodyMap = mutableMapOf<String, RequestBody>()
        params?.let {
            for ((key, value) in it) {
                requestBodyMap[key] = value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        return uploadService.uploadFile(uploadUrl, filePart, requestBodyMap)
    }

}