package com.dudu.upush

/**
 * 功能介绍
 * Created by Dzc on 2023/8/7.
 */
class UPushConstant {
    companion object {
        /**
         * 应用申请的Appkey
         */
        val APP_KEY = "64d1035e6246dd15ebcc5ce1"

        /**
         * 应用申请的UmengMessageSecret
         */
        val MESSAGE_SECRET = "a2a9c20a963d221b46410d2ce20bed1f"

        /**
         * 后台加密消息的密码（仅Demo用，请勿将此密码泄漏）
         */
        val APP_MASTER_SECRET = "ulqu1r9ohgenhdbimz5hbxtc1oclgzhg"

        /**
         * 渠道名称，修改为您App的发布渠道名称
         */
        val CHANNEL = "DuDemo"
    }
}