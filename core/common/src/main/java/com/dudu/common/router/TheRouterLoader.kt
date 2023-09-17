package com.dudu.common.router

import com.dudu.common.BuildConfig
import com.therouter.TheRouter

/**
 * 功能介绍
 * Created by Dzc on 2023/9/15.
 */
class TheRouterLoader {
    companion object{
        fun init(){
            TheRouter.isDebug = BuildConfig.DEBUG
        }
    }
}