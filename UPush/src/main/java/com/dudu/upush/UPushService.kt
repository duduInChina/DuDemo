package com.dudu.upush

import android.content.Context
import android.content.Intent
import com.dudu.common.ext.logD
import com.umeng.message.UmengMessageService
import com.umeng.message.entity.UMessage
import org.android.agoo.common.AgooConstants
import org.json.JSONObject

/**
 * 自定义接收并处理友盟推送消息的服务类
 * Created by Dzc on 2023/8/8.
 */
class UPushService : UmengMessageService() {
    override fun onMessage(context: Context, intent: Intent) {
        "UPushService onMessage".logD()

        val body = intent.getStringExtra(AgooConstants.MESSAGE_BODY)
        body?.let {
            val message = UMessage(JSONObject(it))
            "UPushService message: ${message.raw}".logD()
            if (UMessage.DISPLAY_TYPE_NOTIFICATION == message.display_type) {
                // 处理通知消息
            } else if (UMessage.DISPLAY_TYPE_CUSTOM == message.display_type) {
                // 开发者实现：处理自定义透传消息
            }
        }
    }
}