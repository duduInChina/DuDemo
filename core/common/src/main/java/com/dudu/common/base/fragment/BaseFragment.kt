package com.dudu.common.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.dudu.common.R
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.base.view.BaseView
import com.dudu.common.base.view.ContentViewController
import com.dudu.common.base.view.LoadingViewController
import com.dudu.common.bean.FailedViewModel
import com.dudu.common.databinding.ViewCollapsingTitleBinding
import com.dudu.common.databinding.ViewRootBinding
import com.dudu.common.util.ContextManager
import com.dudu.common.util.DensityUtils
import com.dudu.common.util.StatusBarUtil
import java.lang.reflect.ParameterizedType

/**
 * 功能介绍
 * Created by Dzc on 2023/5/13.
 */
open class BaseFragment<VB : ViewBinding> : Fragment(), BaseView {

    private lateinit var contentViewController: ContentViewController<VB>

    val rootBanding
        get() = contentViewController.rootBinding

    val bodyBinding
        get() = contentViewController.bodyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentViewController = ContentViewController<VB>(
            this, inflater, container, this::class.annotations
        )
        contentViewController.createView()
        return rootBanding.root
    }

    override fun goBack() {
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        contentViewController.clean()
    }

    /**
     * 通常进行immersiveStatusBar，把布局顶到状态栏，才会按需设置
     */
    fun showStatusBarSub(@ColorRes backgroundColor: Int) {
        rootBinding {
            statusBarStub.setOnInflateListener { _, view ->
                view.layoutParams.height =
                    StatusBarUtil.getStatusBarHeight(ContextManager.getContext())
                view.setBackgroundResource(backgroundColor)
            }
            statusBarStub.inflate()
        }
    }

    open fun rootBinding(block: ViewRootBinding.() -> Unit) {
        rootBanding.run(block)
    }

    open fun bodyBinding(block: VB.() -> Unit) {
        contentViewController.bodyBinding.run(block)
    }

    private val loadingViewController by lazy {
        LoadingViewController(requireActivity(), rootBanding)
    }

    override fun loadingView() {
        loadingViewController.loadingView()
    }

    override fun loadingDialog() {
        loadingViewController.loadingDialog()
    }

    override fun hideLoading() {
        loadingViewController.hideLoading()
    }

    override fun getFailedViewBinding(): ViewBinding? = contentViewController.failedViewBinding

    override fun showFailedView(failedViewModel: FailedViewModel) =
        contentViewController.showFailedView(failedViewModel)


}