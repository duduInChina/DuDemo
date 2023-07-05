package com.dudu.datastore

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * 功能介绍
 * Created by Dzc on 2023/5/17.
 */
class DataStoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        IDataStoreOwner.application = context as Application
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}