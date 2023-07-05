package com.dudu.common.base.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.dudu.common.base.viewmodel.BaseViewModel
import com.dudu.common.base.view.BaseVMView
import com.dudu.common.bean.FailedViewModel
import java.lang.reflect.ParameterizedType

/**
 * 功能介绍
 * Created by Dzc on 2023/5/14.
 */
abstract class BaseVMFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>(),
    BaseVMView {

    @Suppress("UNCHECKED_CAST") // 忽略 as VB 转型警告
    protected val viewModel: VM by lazy {
        val type = javaClass.genericSuperclass
        val modelClass: Class<VM> = (type as ParameterizedType).actualTypeArguments[1] as Class<VM>
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[modelClass]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initParam()
        lifecycle.addObserver(viewModel)
        initLoadingObserver()
        initView()
        initFlow()
    }

    override fun initParam() {

    }

    // 注册BaseViewModel的LiveData
    private fun initLoadingObserver() {
        viewModel.loadingViewLiveData.observe(viewLifecycleOwner) {
            if (it) {
                loadingView()
            }
        }
        viewModel.loadingDialogLiveData.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog()
            }
        }
        viewModel.hideLadingLiveData.observe(viewLifecycleOwner) {
            if (it) {
                hideLoading()
            }
        }
        viewModel.failedViewLiveData.observe(viewLifecycleOwner) {
            showFailedView(it)
        }
    }
}