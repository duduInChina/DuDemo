package com.dudu.common.base.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.dudu.common.base.view.BaseVMView
import com.dudu.common.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * 功能介绍
 * Created by Dzc on 2023/5/13.
 */
abstract class BaseVMActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity<VB>(),
    BaseVMView {

    @Suppress("UNCHECKED_CAST") // 忽略 as VB 转型警告
    val viewModel: VM by lazy {
        val type = javaClass.genericSuperclass
        val modelClass: Class<VM> = (type as ParameterizedType).actualTypeArguments[1] as Class<VM>
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[modelClass]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
        lifecycle.addObserver(viewModel) // viewmodel监听，activity事件
        initLoadingObserver()
        initView()
        initFlow()
    }

    override fun initParam() {

    }

    // 注册BaseViewModel的LiveData
    private fun initLoadingObserver() {
        viewModel.loadingViewLiveData.observe(this) {
            if (it) {
                loadingView()
            }
        }
        viewModel.loadingDialogLiveData.observe(this) {
            if (it) {
                loadingDialog()
            }
        }
        viewModel.hideLadingLiveData.observe(this) {
            if (it) {
                hideLoading()
            }
        }
        viewModel.failedViewLiveData.observe(this) {
            showFailedView(it)
        }
    }


}
