package com.dudu.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 非前台服务通知情况
 * app切换到后台，并不断打开其他app，可以持续1分钟
 * 小米 12 app切后台、息屏，下载均持续了1分钟
 *
 * 使用前台服务通知情况
 * 可以持续下载完成，息屏状态下也可以保持下载
 * 主动杀掉通知后还继续下载，后台主动杀程序才停止下载
 * 小米12 取消了通知显示，还可以保证前台服务的下载状态，除非手动后台清理
 * 通常手动后台清理，才会杀死服务
 *
 * Created by Dzc on 2023/5/23.
 */
class DownloadService : Service() {
    private val binder = DownloadBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class DownloadBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }


//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // 每次startServer都会执行
//
////        GlobalScope.launch(Dispatchers.Main + CoroutineName("app")) {
////
////        }
//
//        serviceScope.launch {
//            DownloadRepository.download(
//                "https://imtt.dd.qq.com/16891/myapp/channel_102497291_1165285_b0e7c10e19b54b31dfaf1fd4c0150e44.apk",
//                applicationContext.externalCacheDir!!.absolutePath + "/qq.apk"
//            )
//        }
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
    override fun onCreate() {
//        super.onCreate()
//        // 创建时，创建前台Service，不容易回收
//
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channel = NotificationChannel("my_service", "前台Service通知", NotificationManager.IMPORTANCE_DEFAULT)
//        manager.createNotificationChannel(channel)
//        val intent = Intent(this, DownloadService::class.java)
//        // PendingIntent.FLAG_IMMUTABLE 不可变 pi 不变
//        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val notification = NotificationCompat.Builder(this, "my_service")
//            .setContentTitle("This is content title")
//            .setContentText("This is content text")
//            .setSmallIcon(R.drawable.ic_download_notification)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_download_notification))
//            .setContentIntent(pi)
//            .build()
//        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        DownloadManager.cancel()
    }

}