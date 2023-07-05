package com.dudu.common.base.view

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import com.dudu.common.R
import com.dudu.common.databinding.LayoutLoadingBinding
import com.dudu.common.databinding.ViewRootBinding
import com.dudu.common.widget.MiniLoadingDialog
import com.dudu.common.widget.MiniLoadingView

/**
 * 功能介绍
 * Created by Dzc on 2023/5/15.
 */
class LoadingViewController(
    private val activity: Activity,
    private val rootBinding: ViewRootBinding
) {
    private var isLoagingViewInflate = false
    private var dialog: MiniLoadingDialog? = null

    private lateinit var loadingBinding: LayoutLoadingBinding

    fun loadingView() {
        rootBinding.layoutBody.run {
            if (isLoagingViewInflate) {
                loadingBinding.loadingFrame.visibility = View.VISIBLE
            } else {
                rootBinding.loadingStub.setOnInflateListener { _, view ->
                    loadingBinding = LayoutLoadingBinding.bind(view)
                    loadingBinding.run {
                        isLoagingViewInflate = true
                        loadingFrame.visibility = View.VISIBLE
                    }
                }
                rootBinding.loadingStub.inflate()
            }
        }
    }

    fun loadingDialog() {
        dialog?.let {
            if (it.isShowing) {
                return
            }
        }

        dialog = MiniLoadingDialog(activity)
        dialog?.show()
    }

    fun hideLoading() {
        dialog?.let {
            it.dismiss()
            dialog = null
        }
        if (isLoagingViewInflate) {
            loadingBinding.loadingFrame.visibility = View.GONE
        }
    }

}