package com.dudu.download.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dudu.common.util.FileUtil
import com.dudu.download.BuildConfig
import com.dudu.download.bean.DownloadStatus
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/6/28.
 */
@Entity(tableName = "download_task")
data class DownloadTaskData(
    @PrimaryKey
    var id: Int? = null,
    var url: String,
    var fileName: String? = null,
    var localFilePath: String? = null,
    var status: Int = 0,
    // 忽略本地已存在文件，默认不忽略 启用断点续传
    var ignoreLocal: Boolean = false,
    var autoInstall: Boolean = false,
    var showNotification: Boolean = false,
    var foregroundService: Boolean = false,
    // 通知标题
    var notificationTitle: String? = null,
    // 原文件长度，从请求文件接口读取，判断是否断点续传，不清楚文件是否完整，删除文件再下载，如：长度-1，但文件存在，无法判断文件是否完整续传（删除重新下）
    var contentLength: Long = -1L,
    // 已读取的长度
    var readLength: Long = 0L,
    // 失败信息
    var failedMessage: String? = null,
    var createTime: Long? = null
) {
    init {
        createTime ?: run {
            createTime = System.currentTimeMillis()
        }

        fileName ?: run {
            fileName = FileUtil.fileNameFormUrl(url)
        }
        localFilePath ?: run {
            localFilePath =
                FileUtil.getAppLocalPath() + File.separator + BuildConfig.DOWNLOAD_FILE_SUFFIX + File.separator + fileName
        }
    }

    fun setStatusForDownloadStatus(downloadStatus: DownloadStatus){
        status = when(downloadStatus){
            is DownloadStatus.None -> 0
            is DownloadStatus.Waiting -> 1
            is DownloadStatus.Downloading ->  2
            is DownloadStatus.Stopped -> 3
            is DownloadStatus.Done -> 4
            is DownloadStatus.Failed -> 5
        }
    }

}
