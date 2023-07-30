package com.dudu.common.initializer

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.dudu.common.crash.XCrashManager
import com.dudu.common.log.CommonLogLoader
import com.dudu.common.util.ContextManager

/**
 * app启动时通过Contentrovider，初始化其他业务，common进行初始化
 * Created by Dzc on 2023/5/23.
 */
class CommonInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        ContextManager.init(context as Application)

        CommonLogLoader.logInit()

        XCrashManager.init()
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()

}