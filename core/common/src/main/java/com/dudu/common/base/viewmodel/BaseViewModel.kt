package com.dudu.common.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dudu.common.bean.FailedViewModel

/**
 * 功能介绍
 * Created by Dzc on 2023/5/12.
 */
open class BaseViewModel : ViewModel(), IBaseViewModel {

    // internal 模块可见
    internal val loadingViewLiveData = MutableLiveData(false)
    internal val loadingDialogLiveData = MutableLiveData(false)
    internal val hideLadingLiveData = MutableLiveData(false)
    val failedViewLiveData = MutableLiveData<FailedViewModel>(FailedViewModel.HiddenView)

}