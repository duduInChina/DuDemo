package com.dudu.download

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.dudu.common.exception.DownloadException
import com.dudu.common.util.ContextManager
import com.dudu.download.bean.DownloadStatus
import com.dudu.download.dao.DownloadDatabase
import com.dudu.download.dao.DownloadTaskData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.SoftReference

/**
 * 功能介绍
 * Created by Dzc on 2023/5/26.
 */
class DownloadTask(var taskData: DownloadTaskData) {

    private val _statusLiveData = MutableLiveData<DownloadStatus>(DownloadStatus.None)
    val statusLiveData: MutableLiveData<DownloadStatus>
        get() = _statusLiveData
    val status: DownloadStatus
        get() = _statusLiveData.value as DownloadStatus

    // 记录加载进度
    var progress: Int = -1

    private var job: Job? = null

    var isDbBing = true
    var downloadScope: CoroutineScope? = null

    init {
        when (taskData.status) {
            0 -> _statusLiveData.value = DownloadStatus.None
            1 -> _statusLiveData.value = DownloadStatus.Waiting
            2 -> _statusLiveData.value = DownloadStatus.Downloading(
                readLength = taskData.readLength,
                countLength = taskData.contentLength
            )

            3 -> _statusLiveData.value = DownloadStatus.Stopped
            4 -> _statusLiveData.value = DownloadStatus.Done(file = File(taskData.localFilePath!!))
            5 -> _statusLiveData.value =
                DownloadStatus.Failed(DownloadException(taskData.failedMessage))
        }
    }

    fun run(): Job? {
        job = downloadScope?.launch(Dispatchers.Main) {
            DownloadRepository.download(this@DownloadTask)
                // collectLatest 如上游发射过快忽略旧数据
                .collectLatest {
                    when (it) {
                        is DownloadStatus.Waiting -> {
                            Log.d("download", "start")
                        }

                        is DownloadStatus.Downloading -> {
                            Log.d("download", "progress: $it")
                            taskData.readLength = it.readLength
                            updateNotification()
                        }

                        is DownloadStatus.Done -> {
                            Log.d("download", "Done: $it")
                            updateNotification()
                            if (taskData.autoInstall) {
                                installApk()
                            }
                        }

                        is DownloadStatus.Failed -> {
                            Log.d("download", "Error: $it")
                            updateNotification()
                        }

                        else -> {}
                    }

                    _statusLiveData.value = it
                    updateTask()
                    refreshViewHolder(it)
                }
        }

        return job
    }

    fun cancel() {
        job?.cancel()
        Log.d("download", "Stopped")
        _statusLiveData.value = DownloadStatus.Stopped
        updateTask()
        refreshViewHolder(status)
    }

    fun failed(e: Throwable) {
        _statusLiveData.value = DownloadStatus.Failed(e)
        updateTask()
        refreshViewHolder(status)
    }

    private fun updateNotification() {
        if (!taskData.showNotification) {
            return
        }
        // 协程中同样会调起更新，由于发起或更新通知，哪里发起卡哪里，高频更新放在携程（而且需要判断相同值不更新减少更新次数），主线程调用单次刷新
        when (status) {
            is DownloadStatus.Downloading -> {
                val percent =
                    if (taskData.contentLength <= 0) 0 else (taskData.readLength / taskData.contentLength.toDouble() * 100).toInt()
                if (progress != percent) {
                    progress = percent
                    DownloadNotification.showNotification(taskData.url.hashCode(), this)
                }
            }

            is DownloadStatus.Done, is DownloadStatus.Failed ->
                DownloadNotification.showNotification(taskData.url.hashCode(), this)

            else -> {}
        }
    }

    private fun updateTask() {

        taskData.setStatusForDownloadStatus(status)

        if (isDbBing) {
            downloadScope?.launch(Dispatchers.Main) {
                Log.d("updateTask", taskData.toString())
                DownloadDatabase.getInstance().downloadTaskDao().updateTask(taskData)
            }
        }

    }

    private fun installApk() {
        // 自动安装
        if (!ContextManager.getContext().packageManager.canRequestPackageInstalls()) {
            Toast.makeText(ContextManager.getContext(), "app未有安装权限", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val uri: Uri = FileProvider.getUriForFile(
            ContextManager.getContext(),
            "${ContextManager.getContext().packageName}${BuildConfig.DOWNLOAD_AUTHORITIES_SUFFIX}",
            File(taskData.localFilePath!!)
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        ContextManager.getContext().startActivity(intent)

    }

    var downloadListener: SoftReference<DownloadListener>? = null

    fun setViewHolderRefreshListener(listener: DownloadListener){
        downloadListener = SoftReference(listener)
    }

    private fun refreshViewHolder(downloadStatus: DownloadStatus) {
        downloadListener?.let {
            it.get()?.refreshStatus(downloadStatus)
        }
    }
}