package com.dudu.common

import com.dudu.common.log.CommonLogLoader

/**
 * 功能介绍
 * Created by Dzc on 2023/7/24.
 */
class CommonConstant {
    companion object{
        // log加载器
        val logLoader by lazy {
            CommonLogLoader()
        }
    }
}