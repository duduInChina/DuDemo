package com.dudu.upush

import android.content.Context
import androidx.startup.Initializer

/**
 * 功能介绍
 * Created by Dzc on 2023/8/7.
 */
class UPushInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        //UPushManager.init(context)
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}