package com.dudu.audio

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.dudu.common.ext.logD
import com.dudu.demoaudio.BuildConfig
import com.dudu.demoaudio.R

/**
 * 桌面小部件
 * https://www.jianshu.com/p/a1ebdcceaad6
 * Created by Dzc on 2023/9/5.
 */
class AudioWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        // 获得更新广播
        "AudioWidget.onReceive ${intent?.action}".logD()

        intent?.let {
            when(it.action){
                BuildConfig.AUDIO_WIDGET_UPDATE -> {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager,
                        appWidgetManager.getAppWidgetIds(
                            ComponentName(context.packageName, this.javaClass.name)
                        ),
                        it.extras
                    )
                }
                else -> {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager,
                        appWidgetManager.getAppWidgetIds(
                            ComponentName(context.packageName, this.javaClass.name)
                        ),
                        it.extras
                    )
                }
            }
        }

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 更新视图
        "AudioWidget.onUpdate ${appWidgetIds.toString()}".logD()
        updateAppWidget(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        // 第一个加入到屏幕上。
        // AudioWidget.onUpdate [I@b9e6e85
        // AudioWidget.onReceive android.appwidget.action.APPWIDGET_UPDATE
        // AudioWidget.onEnabled
        // AudioWidget.onReceive android.appwidget.action.APPWIDGET_ENABLED
        // AudioWidget.onReceive android.appwidget.action.APPWIDGET_UPDATE_OPTIONS
        "AudioWidget.onEnabled".logD()
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        // 最后一个widget从屏幕移除。
        "AudioWidget.onDisabled".logD()
    }

    /**
     * extras 自动更新没有相关参数，获取到通知更新包含相关更新信息
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        extras: Bundle? = null
    ) {

        val remoteViews = RemoteViews(context.packageName, R.layout.layout_audio_widget)

//        以广播模式启动前台服务，亦可以触发
//        remoteViews.setOnClickPendingIntent(
//            R.id.playBtn,
//            PendingIntent.getBroadcast(
//                context, 0,
//                Intent(context, AudioWidget::class.java).setAction(ACTION_PLAY).setPackage(context.packageName),
//                // PendingIntent.FLAG_IMMUTABLE表示创建一个不可变的PendingIntent对象，而PendingIntent.FLAG_UPDATE_CURRENT表示如果PendingIntent已经存在，则使用新的Intent更新该PendingIntent。
//                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        )

        // 启用前台服务触发通知，在规定时间服务中必须启用startForeground
        remoteViews.setOnClickPendingIntent(
            R.id.playBtn,
            PendingIntent.getForegroundService(
                context, 0,
                Intent(context, AudioService::class.java)
                    .setAction(AudioService.ACTION_PLAY),
                // PendingIntent.FLAG_IMMUTABLE表示创建一个不可变的PendingIntent对象，而PendingIntent.FLAG_UPDATE_CURRENT表示如果PendingIntent已经存在，则使用新的Intent更新该PendingIntent。
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        remoteViews.setOnClickPendingIntent(
            R.id.previousBtn,
            PendingIntent.getForegroundService(
                context, 0,
                Intent(context, AudioService::class.java)
                    .setAction(AudioService.ACTION_PREVIOUS),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        remoteViews.setOnClickPendingIntent(
            R.id.nextBtn,
            PendingIntent.getForegroundService(
                context, 0,
                Intent(context, AudioService::class.java)
                    .setAction(AudioService.ACTION_NEXT),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        extras?.let {
            it.getString(AudioService.RECEIVER_TITLE)?.let { title ->
                remoteViews.setTextViewText(R.id.title, title)
            }

            it.getBoolean(AudioService.RECEIVER_PLAYING).let { status ->
                remoteViews.setImageViewResource(R.id.playBtn,
                    if(status) R.drawable.icon_pause else R.drawable.icon_play
                )
            }

        }

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

}