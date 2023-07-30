package com.dudu.common.log

import com.dudu.common.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy

/**
 * 功能介绍
 * Created by Dzc on 2023/7/27.
 */
class LoggerAdapter(formatStrategy: FormatStrategy) : AndroidLogAdapter(formatStrategy) {
    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return BuildConfig.DEBUG
    }
}