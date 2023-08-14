package com.dudu.upush

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.util.Log
import com.dudu.common.BuildConfig
import com.dudu.common.ext.logD
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 功能介绍
 * Created by Dzc on 2023/8/7.
 */
object UPushManager {

    fun init(context: Context) {

        if (BuildConfig.DEBUG) {
            // 日志
            UMConfigure.setLogEnabled(true)
        }

        // 预初始化
        UMConfigure.preInit(context, UPushConstant.APP_KEY, UPushConstant.CHANNEL)

        // 是否同意隐私政策
        // if (!agreed) {
        //    return;
        // }

        if (UMUtils.isMainProgress(context)) {
            //启动优化：建议在子线程中执行初始化
            CoroutineScope(Dispatchers.IO).launch {
                initUMConfigure(context)
            }
        }

    }

    private fun initUMConfigure(context: Context) {

        // 参数一：上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：发布渠道名称；
        // 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
        // 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息
        UMConfigure.init(
            context,
            UPushConstant.APP_KEY,
            UPushConstant.CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            UPushConstant.MESSAGE_SECRET
        )

        val api = PushAgent.getInstance(context)

        api.resourcePackageName = context.packageName

        pushSetting(context)

        // 第三方厂商Token回调
        api.setThirdTokenCallback { type, token ->
            "Umeng push third token  callback type: $type token: $token".logD()
        }

        // 设备注册推送，每次调用回调
        api.register(object : UPushRegisterCallback{
            override fun onSuccess(deviceToken: String) {
                "Umeng push callback register deviceToken: $deviceToken".logD()
            }

            override fun onFailure(errCode: String, errDesc: String) {
                "Umeng push callback failure errCode: $errCode errDesc: $errDesc".logD()
            }
        })

        // 注册设备推送通道（小米、华为等设备的推送）
    }

    private fun pushSetting(context: Context) {
        val api = PushAgent.getInstance(context)
        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        api.displayNotificationNumber = 3

        //推送消息处理
//        api.messageHandler = object : UmengMessageHandler() {
//            //处理通知栏消息
//            override fun dealWithNotificationMessage(context: Context, msg: UMessage) {
//                super.dealWithNotificationMessage(context, msg)
//
//                "Umeng push notification receiver: ${msg.raw}".logD()
//            }
//
//            //自定义通知样式，此方法可以修改通知样式等
//            override fun getNotification(context: Context, msg: UMessage): Notification {
//                return super.getNotification(context, msg)
//            }
//
//            //自定义NotificationChannel
//            override fun getNotificationChannel(): NotificationChannel {
//                return super.getNotificationChannel()
//            }
//
//            //处理透传消息
//            override fun dealWithCustomMessage(context: Context, msg: UMessage) {
//                super.dealWithCustomMessage(context, msg)
//                "Umeng push custom receiver: ${msg.raw}".logD()
//            }
//        }
//
//        api.notificationClickHandler = object : UmengNotificationClickHandler() {
//            override fun openActivity(context: Context, msg: UMessage) {
//                super.openActivity(context, msg)
//                "Umeng push click openActivity: ${msg.raw}".logD()
//            }
//
//            override fun launchApp(context: Context, msg: UMessage) {
//                super.launchApp(context, msg)
//                "Umeng push click launchApp: ${msg.raw}".logD()
//            }
//
//            override fun dismissNotification(context: Context, msg: UMessage) {
//                super.dismissNotification(context, msg)
//                "Umeng push click dismissNotification: ${msg.raw}".logD()
//            }
//
//        }

        // 要不走上面Handler处理，要不走service处理
        // 通过Service自定义接收并处理消息
        api.setPushIntentServiceClass(UPushService::class.java)

    }

}