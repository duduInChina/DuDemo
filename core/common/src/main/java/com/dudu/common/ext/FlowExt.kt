package com.dudu.common.ext

import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.bean.FailedViewModel
import com.dudu.common.exception.ExceptionManager
import com.dudu.common.exception.HttpException
import com.dudu.common.exception.JsonException
import com.dudu.common.exception.NetworkException
import com.dudu.common.exception.ResultException
import com.dudu.common.util.ContextManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.net.HttpRetryException
import java.net.UnknownHostException

/**
 * 通常用于网络请求中的作用域
 * Created by Dzc on 2023/5/12.
 */

fun <T> Flow<T>.lifecycle(
    baseViewModel: BaseViewModel,
    beforeCallback: (() -> Unit)? = null,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    loadingView: Boolean = false,
    loadingDialog: Boolean = false,
    showFailedView: Boolean = false,
    delayTime: Long = 0,
    callback: T.() -> Unit
) {
    baseViewModel.viewModelScope.launch(Dispatchers.Main) {
        this@lifecycle.flowOn(Dispatchers.IO)
            .onStart {
                if (baseViewModel.failedViewLiveData.value !is FailedViewModel.HiddenView) {
                    baseViewModel.failedViewLiveData.value = FailedViewModel.HiddenView
                }
                if (loadingView) {
                    baseViewModel.loadingViewLiveData.value = true
                } else if (loadingDialog) {
                    baseViewModel.loadingDialogLiveData.value = true
                }

                beforeCallback?.let {
                    beforeCallback()
                }
                if(delayTime > 0){
                    delay(delayTime)
                }

            }.catch { t ->
                if (showFailedView) {
                    ExceptionManager.failedLogic(baseViewModel, t, showFailedView)
                }

                errorCallback?.let {
                    errorCallback(t)
                }
            }.onCompletion {
                if (loadingView) {
                    baseViewModel.loadingViewLiveData.value = false
                    baseViewModel.hideLadingLiveData.value = true
                } else if (loadingDialog) {
                    baseViewModel.loadingDialogLiveData.value = false
                    baseViewModel.hideLadingLiveData.value = true
                }
            }.collect {
                callback(it)
            }
    }
}


fun <T> Flow<T>.lifecycleLoadingView(
    baseViewModel: BaseViewModel,
    beforeCallback: (() -> Unit)? = null,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    callback: T.() -> Unit
) {
    baseViewModel.viewModelScope.launch(Dispatchers.Main) {
        this@lifecycleLoadingView.flowOn(Dispatchers.IO)
            .onStart {
                beforeCallback?.let {
                    baseViewModel.loadingViewLiveData.value = true
                    beforeCallback()
                }
            }.onCompletion {
                baseViewModel.loadingViewLiveData.value = false
                baseViewModel.hideLadingLiveData.value = true
            }.catch { t ->
                baseViewModel.loadingViewLiveData.value = false
                baseViewModel.hideLadingLiveData.value = true
                errorCallback?.let {
                    errorCallback(t)
                }
            }.collect {
                callback(it)
            }
    }
}

fun <T> Flow<T>.lifecycleLoadingDialog(
    baseViewModel: BaseViewModel,
    beforeCallback: (() -> Unit)? = null,
    errorCallback: ((t: Throwable) -> Unit)? = null,
    callback: T.() -> Unit
) {
    baseViewModel.viewModelScope.launch(Dispatchers.Main) {
        this@lifecycleLoadingDialog.flowOn(Dispatchers.IO)
            .onStart {
                beforeCallback?.let {
                    baseViewModel.loadingDialogLiveData.value = true
                    beforeCallback()
                }
            }.onCompletion {
                baseViewModel.loadingDialogLiveData.value = false
                baseViewModel.hideLadingLiveData.value = true
            }.catch { t ->
                baseViewModel.loadingDialogLiveData.value = false
                baseViewModel.hideLadingLiveData.value = true
                errorCallback?.let {
                    errorCallback(t)
                }
            }.collect {
                callback(it)
            }
    }
}

