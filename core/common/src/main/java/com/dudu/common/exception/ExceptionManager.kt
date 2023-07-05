package com.dudu.common.exception

import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.bean.FailedViewModel
import com.dudu.common.util.ContextManager

/**
 * 功能介绍
 * Created by Dzc on 2023/6/26.
 */
class ExceptionManager {
    companion object{
        fun failedLogic(baseViewModel: BaseViewModel, t: Throwable, showFailedView: Boolean = false) {
            when (t) {
                is NetworkException ->
                    if (showFailedView)
                        baseViewModel.failedViewLiveData.value = FailedViewModel.NetworkError
                    else ContextManager.showToast(FailedViewModel.NetworkError.failedText)

                is HttpException ->
                    if (showFailedView)
                        baseViewModel.failedViewLiveData.value = FailedViewModel.HttpError
                    else ContextManager.showToast(FailedViewModel.HttpError.failedText)

                is JsonException ->
                    if (showFailedView)
                        baseViewModel.failedViewLiveData.value = FailedViewModel.DataError
                    else ContextManager.showToast("数据解析异常")

                is ResultException ->
                    if (showFailedView)
                        baseViewModel.failedViewLiveData.value = FailedViewModel.DataError
                    else ContextManager.showToast(t.message ?: "获取数据异常")
            }
        }
    }
}