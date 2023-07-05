package com.dudu.download

import com.dudu.download.bean.DownloadStatus
import java.lang.ref.SoftReference

/**
 * 功能介绍
 * Created by Dzc on 2023/6/30.
 */
interface DownloadListener {
   fun refreshStatus(status: DownloadStatus)
}

abstract class AbsDownloadListener(var userTag: SoftReference<Any>) : DownloadListener{

}