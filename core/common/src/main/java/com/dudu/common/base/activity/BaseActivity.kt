package com.dudu.common.base.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.dudu.common.util.DensityUtils
import java.lang.reflect.ParameterizedType

/**
 * 功能介绍
 * Created by Dzc on 2023/5/13.
 */

open class BaseActivity<VB : ViewBinding> : AppCompatActivity(), BaseView {

    private val contentViewController by lazy {
        ContentViewController<VB>(
            baseView = this,
            inflater = layoutInflater,
            annotations = this::class.annotations
        )
    }

    val rootBanding
        get() = contentViewController.rootBinding

    val bodyBinding
        get() = contentViewController.bodyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setContentView(rootBanding.root)
        super.onCreate(savedInstanceState)
        contentViewController.createView()
    }

    override fun goBack() {
        finish()
    }

    open fun rootBinding(block: ViewRootBinding.() -> Unit) {
        rootBanding.run(block)
    }

    open fun bodyBinding(block: VB.() -> Unit) {
        contentViewController.bodyBinding.run(block)
    }

    private val loadingViewController by lazy {
        LoadingViewController(this, rootBanding)
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
