package com.dudu.download

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.dudu.common.util.ContextManager
import com.dudu.common.util.FileUtil
import com.dudu.download.bean.DownloadStatus
import com.dudu.download.dao.DownloadDatabase
import com.dudu.download.dao.DownloadTaskData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

/**
 * 功能介绍
 * Created by Dzc on 2023/5/26.
 */
object DownloadManager {

    var downloadService: DownloadService? = null

    private val downloadApkScope = CoroutineScope(Dispatchers.Main)// apk下载独立作用域，单独启动取消
    private val downloadScope = CoroutineScope(Dispatchers.Main)// 下载作用域

    var apkDownLoadTask: DownloadTask? = null
    val taskListLiveData = MutableLiveData<MutableList<DownloadTask>>(mutableListOf())

    init {
        downloadScope.launch(Dispatchers.Main) {
            val list = DownloadDatabase.getInstance().downloadTaskDao().queryTask()
            val taskList = taskListLiveData.value.orEmpty().toMutableList()
            for (item in list) {
                val downloadTask = DownloadTask(item)
                downloadTask.downloadScope = downloadScope
                taskList.add(downloadTask)
                Log.d("DownloadManagerInit", item.toString())
            }
            taskListLiveData.value = taskList

        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DownloadService.DownloadBinder
            downloadService = binder.getService()

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            downloadService = null
        }
    }

    //////////// Apk ///////////////
    fun downloadApk(context: Context, task: DownloadTask) {
        // 是否存在正在执行的task
        apkDownLoadTask?.let {
            if (it.status is DownloadStatus.None ||
                it.status is DownloadStatus.Waiting ||
                it.status is DownloadStatus.Downloading ||
                it.status is DownloadStatus.Stopped
            ) {
                ContextManager.showToast("已存在未执行完成的Apk下载任务")
                return
            }
        }

        checkServiceStart(context)
        task.isDbBing = false
        task.downloadScope = downloadApkScope
        apkDownLoadTask = task
        task.run()
    }

    fun stopDownloadApk() {
        apkDownLoadTask?.cancel()
    }

    // 继续下载
    fun continueDownloadApk() {
        apkDownLoadTask?.let {
            if (it.status == DownloadStatus.Stopped) {
                it.run()
            }
        }
    }

    // 重新下载
    fun resumeDownloadApk() {
        apkDownLoadTask?.let {
            when (it.status) {
                is DownloadStatus.Done, is DownloadStatus.Failed -> {
                    // 重新下载，删除原文件
                    if (it.taskData.ignoreLocal) {
                        val file = File(it.taskData.localFilePath!!)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    it.taskData.readLength = 0L
                    it.run()
                }

                else -> {}
            }
        }
    }

    //////////// Task /////////////////
    fun download(context: Context, taskData: DownloadTaskData) {
        val taskList = taskListLiveData.value.orEmpty().toMutableList()
        for (cacheTask in taskList) {
            if (cacheTask.taskData.url == taskData.url &&
                cacheTask.status !is DownloadStatus.Done &&
                cacheTask.status !is DownloadStatus.Failed) {
                ContextManager.showToast("已存在相同url任务")
                return
            }
        }

        downloadScope.launch(Dispatchers.Main) {
            flow<DownloadTaskData>{
                var id = DownloadDatabase.getInstance().downloadTaskDao().insertTask(taskData)
                emit(DownloadDatabase.getInstance().downloadTaskDao().queryTaskId(id))
            }.flowOn(Dispatchers.IO)
                .collect{
                    checkServiceStart(context)
                    val task = DownloadTask(it)
                    task.downloadScope = downloadScope
                    taskList.add(0, task)
                    taskListLiveData.value = taskList
                    task.run()
                }

        }


    }

    // 暂停
    fun stopDownloadTask(task: DownloadTask) {
        task.cancel()
    }

    // 继续下载
    fun continueDownloadTask(task: DownloadTask) {
        if (task.status == DownloadStatus.Stopped) {
            task.run()
        }
    }

    // 重新下载
    fun resumeDownloadTask(task: DownloadTask) {
        when (task.status) {
            is DownloadStatus.Done, is DownloadStatus.Failed -> {
                // 重新下载，删除原文件
                val file = File(task.taskData.localFilePath!!)
                if (file.exists()) {
                    file.delete()
                }
                task.taskData.readLength = 0L
                task.run()
            }

            else -> {}
        }
    }

    /////////// service ///////////////
    fun checkServiceStart(context: Context) {
        if (downloadService == null || !isServiceRunning(context, DownloadService::class.java)) {
            // 启动service
            val serviceIntent = Intent(context, DownloadService::class.java)
            context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun getDownloadFilePath(fileName: String): String {
        return FileUtil.getAppLocalPath() + "/download/$fileName"
    }

    fun cancel() {
        downloadScope.cancel()
    }

}