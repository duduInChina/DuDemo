package com.dudu.download.bean

import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/6/27.
 */
sealed class DownloadStatus{
    // 0:任务创建 - 无状态
    object None : DownloadStatus()
    // 1:进入请求等待
    object Waiting : DownloadStatus()
    // 2:正在加载
    data class Downloading(
        /**
         * 当前文件读取长度
         */
        var readLength: Long,
        /**
         * 当前文件总长度
         */
        var countLength: Long,
        /**
         * 完成文件数
         */
        var completeCount: Int = 1,
        /**
         * 总文件数
         */
        var totalCount: Int = 1
    ) : DownloadStatus()
    // 3:未下载完成暂停
    object Stopped : DownloadStatus()
    // 4:完成
    data class Done(val file: File): DownloadStatus()
    // 5:失败
    data class Failed(val e: Throwable) : DownloadStatus()

}
