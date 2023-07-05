package com.dudu.download

import android.util.Log
import com.dudu.download.bean.DownloadStatus
import com.dudu.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

/**
 * 功能介绍
 * Created by Dzc on 2023/5/22.
 */
object DownloadRepository {

    // 由于下载的baseurl未必是一个确定值，所以根据下载链接再次切换调整
    private const val BASEURL = "https://example.com"

    private val downloadService by lazy {
        RetrofitManager.createService(DownloadApi::class.java, BASEURL)
    }

    suspend fun download(task: DownloadTask) = flow<DownloadStatus> {
        val file = File(task.taskData.localFilePath!!)
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdirs()
        }
        var currentLength = 0L // 当前文件是否存在
        val ignoreLocal = if(task.status == DownloadStatus.Stopped) false else task.taskData.ignoreLocal // 是否忽略文件，任务暂停启动不忽略
        if(file.exists()){
            if(ignoreLocal || task.taskData.contentLength == -1L){
                // 忽略原文件，删除 或 不清楚文件是否完整，删除文件再下载
                file.delete()
            }else{
                currentLength = file.length()
            }
        }

        if(!ignoreLocal && currentLength > 0 && currentLength == task.taskData.contentLength){
            // 不忽略原文件，原文件存在，且长度一致直接返回
            emit(DownloadStatus.Done(file))
        }else {

            val headers = mutableMapOf<String, String>()
            if (currentLength > 0) {
                // 启用断点续传
                headers["Range"] = "bytes=$currentLength-"
            }

            emit(DownloadStatus.Waiting)

            val responseBody = downloadService.coroutinesDownload(task.taskData.url, headers)
            if (!isFile(responseBody)) {
                throw IllegalArgumentException("下载长度异常:" + responseBody.contentLength())
            } else {
                if(currentLength == 0L){
                    task.taskData.contentLength = responseBody.contentLength()
                }
                val inputStream = responseBody.byteStream()
                val buffer = ByteArray(1024 * 8)
                var len: Int
                var readLength = currentLength
                val countLength = responseBody.contentLength()

                FileOutputStream(file, currentLength > 0).use { output ->
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        output.write(buffer, 0, len)
                        readLength += len
                        emit(DownloadStatus.Downloading(readLength, countLength))
                    }
                    output.flush()
                }
                inputStream.close()
                emit(DownloadStatus.Done(file))
            }
        }
    }.catch {
        emit(DownloadStatus.Failed(it))
    }.flowOn(Dispatchers.IO)

    private fun isFile(responseBody: ResponseBody): Boolean{
        val countLength = responseBody.contentLength()
//        val contentType = responseBody.contentType()
        return countLength != -1L
    }

}