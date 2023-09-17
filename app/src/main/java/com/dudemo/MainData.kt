package com.dudemo

import android.content.Intent

/**
 * 主页面数据
 * Created by Dzc on 2023/6/22.
 */
data class MainData(val title: String, val content: String,  val intent: Intent? = null, val routerPath: String ?= null)
