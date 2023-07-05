package com.dudu.common.base.view

import android.util.Log
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dudu.common.bean.FailedViewModel

/**
 * 功能介绍
 * Created by Dzc on 2023/5/13.
 */
interface BaseView {

    fun goBack()

    fun loadingView()

    fun loadingDialog()

    fun hideLoading()

    fun getFailedViewBinding(): ViewBinding?

    fun showFailedView(failedViewModel: FailedViewModel)

    /**
     * 点击失败页面,回调
     */
    fun onFailedViewReload(){
        Log.d("FailedView", "click failed view reload")
    }

    /**
     * FailedView加载到的目标view
     */
    fun onFailedViewTarget(): ViewGroup? = null

}