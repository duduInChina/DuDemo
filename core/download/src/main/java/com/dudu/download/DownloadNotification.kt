package com.dudu.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dudu.common.util.ContextManager
import com.dudu.common.util.FileUtil
import com.dudu.download.bean.DownloadStatus

/**
 * 功能介绍
 * Created by Dzc on 2023/5/30.
 */
class DownloadNotification {
    // 参考博客： https://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650242466&idx=1&sn=2f77d560ac7a82a864d905ee1c95117c&chksm=88638ccdbf1405dba8e4d72077c5174ecf338b2501c83c43093044915b5e3b9a1eef3f219d49&scene=27
    // https://mp.weixin.qq.com/s/d1VUOgcgLY-EQJTafvyC7A

    companion object{
        private const val CHANNEL_ID = "download_channel_normal"

        private fun getNotificationManager(): NotificationManager {
            val notificationManager = ContextManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.getNotificationChannel(CHANNEL_ID)?:let{
                // 注册应用通知渠道： 渠道id 、 渠道名称 、 重要等级
                val channel = NotificationChannel(CHANNEL_ID, "下载", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            return notificationManager
        }

        fun showNotification(id: Int, task: DownloadTask){

            val notificationManager = getNotificationManager()

            val builder = NotificationCompat.Builder(ContextManager.getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download_notification)
                .setContentTitle(task.taskData.notificationTitle?:task.taskData.fileName)
                .setAutoCancel(task.status is DownloadStatus.Done)// 点击取消
                .setOngoing(task.status is DownloadStatus.Downloading)// true: 左右滑动不能取消通知

            when(task.status){
                is DownloadStatus.Downloading -> {
                    Log.d("DownloadNotification", task.progress.toString())
                    val s = FileUtil.formatFileSize(task.taskData.readLength) + "/" + FileUtil.formatFileSize(task.taskData.contentLength)
                    builder.setContentText(s)
//                    val percent = if(task.contentLength <= 0) 0 else (task.readLength/task.contentLength.toDouble() * 100).toInt()
                    builder.setProgress(100, task.progress, task.progress <= 0)// indeterminate 是否显示加载
                }

                is DownloadStatus.Done -> {
                    builder.setContentText("下载完成")
                    builder.setProgress(100, 100, false)
                    if(task.taskData.autoInstall){
                        // 点击安装
                    }
                }

                is DownloadStatus.Failed -> {
                    // 通知重新下载
                    builder.setContentText("下载失败")
                }

                else -> {}
            }


            if(task.taskData.foregroundService && DownloadManager.downloadService != null){
                // 多个不同id，只会显示第一个（最后跟新的），多任务状态下统一id
                // TODO 关闭通知等，检查service是否启用，初始时app是否在前台（才执行的否则异常）
                DownloadManager.downloadService?.startForeground(id, builder.build())
            }else{
                notificationManager.notify(id, builder.build())
            }



        }

        fun cancelNotification(id: Int){
            getNotificationManager().cancel(id)
        }

    }

}