package com.dudu.common.util

import android.app.Application
import android.content.Context
import android.widget.Toast

/**
 * 功能介绍
 * Created by Dzc on 2023/5/23.
 */
object ContextManager {
    private lateinit var context: Application

    fun init(context: Application){
        this.context = context
    }

    fun getContext(): Context{
        return context
    }

    fun showToast(text: CharSequence){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

}