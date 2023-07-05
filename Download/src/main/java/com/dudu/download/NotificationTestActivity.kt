package com.dudu.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.dudu.common.base.activity.BaseActivity
import com.dudu.common.util.ContextManager
import com.dudu.download.databinding.ActivityNotificationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * 功能介绍
 * Created by Dzc on 2023/5/31.
 */
class NotificationTestActivity : BaseActivity<ActivityNotificationBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bodyBinding {
            testBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    flow {
                        val fora = 0..10000
                        var progress = 0
                        for (i in fora) {
                            // 主线程更新notification出现anr，子线程更新导致执行变慢
                            val p = (i/10000.0 * 100).toInt()
                            if(p != progress){
                                progress = p
                                Log.d("NotificationTestActivity", i.toString())
                                showNotification(progress)
                            }

                            emit(i)
//                            kotlinx.coroutines.delay(100)
                        }



                    }.flowOn(Dispatchers.IO)
                        .collect {
                            // 更新ui正常
                            testBtn.text = it.toString()

                        }
                }


            }

        }

    }

    private val CHANNEL_TEST_ID = "test_channel_normal"

    private fun getNotificationManager(): NotificationManager {
        return ContextManager.getContext()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showNotification(percent: Int) {

        val notificationManager = getNotificationManager()

        // 注册应用通知渠道： 渠道id 、 渠道名称 、 重要等级
        val channel =
            NotificationChannel(CHANNEL_TEST_ID, "测试", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val bulider = NotificationCompat.Builder(
            ContextManager.getContext(),
            CHANNEL_TEST_ID
        )
            .setSmallIcon(R.drawable.ic_notification_test)
            .setContentTitle("Test")

        bulider.setSubText(percent.toString())
//        val progress = (percent/10000.0 * 100).toInt()
        bulider.setProgress(100, percent, percent <= 0)

        if(percent == 100){
            bulider.setContentText("下载失败")
        }

        notificationManager.notify(1, bulider.build())

    }

}