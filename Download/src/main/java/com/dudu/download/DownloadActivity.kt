package com.dudu.download

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudu.common.base.activity.BaseVMActivity
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.router.RouterPath
import com.dudu.common.util.ContextManager
import com.dudu.download.DownloadConstant.Companion.apkUrl
import com.dudu.download.DownloadConstant.Companion.urlList
import com.dudu.download.bean.DownloadStatus
import com.dudu.download.dao.DownloadTaskData
import com.dudu.download.databinding.ActivityDownloadBinding
import com.therouter.router.Route

/**
 * 功能介绍
 * Created by Dzc on 2023/5/21.
 */
@Title("下载", true, TitleType.COLL)
@Route(path = RouterPath.DOWNLOAD)
class DownloadActivity : BaseVMActivity<ActivityDownloadBinding, DownloadViewModel>() {

    private var apkTask: DownloadTask? = null

    private lateinit var adapter: DownloadAdapter

    private var autoUrlIndex = 0

    override fun initView() {

        infoTask()
        bodyBinding {
            recyclerView.layoutManager = LinearLayoutManager(this@DownloadActivity)
            adapter = DownloadAdapter()
            adapter.set(DownloadManager.taskListLiveData.value)
            recyclerView.adapter = adapter

            apkDownloadBtn.setOnClickListener {

                if (apkAutoInstall.isChecked) {
                    if (!ContextManager.getContext().packageManager.canRequestPackageInstalls()) {
                        // TODO 优化
                        val intentString = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + this@DownloadActivity.packageName)
                        )
                        this@DownloadActivity.startActivity(intentString)
                        return@setOnClickListener
                    }
                }

                if (apkTask == null) {
                    apkTask = DownloadTask(DownloadTaskData(
                        url = apkUrl,
                        ignoreLocal = apkIgnore.isChecked,
                        autoInstall = apkAutoInstall.isChecked,
                        showNotification = apkNotification.isChecked,
                        foregroundService = apkService.isChecked,
                        notificationTitle = "Apk下载"
                    ))
                    apkTask?.let {
                        infoTaskFlow()
                        // 启动下载
                        DownloadManager.downloadApk(this@DownloadActivity, it)
                    }
                } else {
                    apkTask?.let {
                        when (it.status) {
                            is DownloadStatus.None -> DownloadManager.downloadApk(
                                this@DownloadActivity,
                                it
                            )

                            is DownloadStatus.Waiting -> DownloadManager.stopDownloadApk()
                            is DownloadStatus.Downloading -> DownloadManager.stopDownloadApk()
                            is DownloadStatus.Stopped -> DownloadManager.continueDownloadApk()
                            is DownloadStatus.Done, is DownloadStatus.Failed -> {
                                it.taskData.ignoreLocal = apkIgnore.isChecked
                                it.taskData.autoInstall = apkAutoInstall.isChecked
                                it.taskData.showNotification = apkNotification.isChecked
                                it.taskData.foregroundService = apkService.isChecked
                                DownloadManager.resumeDownloadApk()
                            }
                        }
                    }

                }
            }

            addTaskEditText.hint = apkUrl

            addTaskBtn.setOnClickListener {
                val url = addTaskEditText.text.toString().ifEmpty {
                    apkUrl
                }

                DownloadManager.download(
                    this@DownloadActivity,
                    DownloadTaskData(
                        url = url,
                        fileName = System.currentTimeMillis().toString(),
                        ignoreLocal = taskIgnore.isChecked,
                        showNotification = taskNotification.isChecked,
                        foregroundService = taskService.isChecked,
                    )
                )
            }

            autoAddTaskBtn.setOnClickListener {
                val index = autoUrlIndex % urlList.size
                ++autoUrlIndex
                DownloadManager.download(
                    this@DownloadActivity,
                    DownloadTaskData(
                        url = urlList[index],
                        fileName = System.currentTimeMillis().toString(),
                        ignoreLocal = taskIgnore.isChecked,
                        showNotification = taskNotification.isChecked,
                        foregroundService = taskService.isChecked,
                    )
                )
            }

        }


    }

    private fun infoTaskFlow() {
        apkTask?.let {
            it.statusLiveData.observe(this) { downloadStatus ->
                when (downloadStatus) {
                    is DownloadStatus.Downloading -> {
                        bodyBinding.apkSize.text = DownloadConstant.fileSizeString(it.taskData)
                        bodyBinding.apkProgressBar.progress = DownloadConstant.downloadProgress(it.taskData)
                    }

                    is DownloadStatus.Done -> {
                        bodyBinding.apkSize.text = DownloadConstant.fileSizeString(it.taskData)
                        bodyBinding.apkProgressBar.progress = 100
                        resetApkBtn()
                    }

                    is DownloadStatus.None, is DownloadStatus.Waiting,
                    is DownloadStatus.Stopped, is DownloadStatus.Failed -> {
                        resetApkBtn()
                    }
                }
            }
        }
    }

    private fun infoTask() {
        apkTask = DownloadManager.apkDownLoadTask
        apkTask?.let {
            resetApkBtn()
            if (it.taskData.contentLength > 0) {
                bodyBinding.apkSize.text = DownloadConstant.fileSizeString(it.taskData)
                bodyBinding.apkProgressBar.progress = DownloadConstant.downloadProgress(it.taskData)
            }
            infoTaskFlow()
        }
    }


    private fun resetApkBtn() {
        bodyBinding {
            apkTask?.run {
                apkDownloadBtn.text = DownloadConstant.downloadStatusToText(status)
            }
        }
    }


    override fun initFlow() {
        DownloadManager.taskListLiveData.observe(this) {
            adapter.set(DownloadManager.taskListLiveData.value)
        }
    }


}