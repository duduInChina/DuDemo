package com.dudu.common.base.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.contains
import androidx.viewbinding.ViewBinding
import com.dudu.common.R
import com.dudu.common.base.annotation.Title
import com.dudu.common.base.annotation.TitleType
import com.dudu.common.bean.FailedViewModel
import com.dudu.common.databinding.ViewCollapsingTitleBinding
import com.dudu.common.databinding.ViewDefaultTitleBinding
import com.dudu.common.databinding.ViewFailedBinding
import com.dudu.common.databinding.ViewRootBinding
import com.dudu.common.util.ContextManager
import com.dudu.common.util.DensityUtils
import java.lang.reflect.ParameterizedType

/**
 * 根布局管理类
 * Created by Dzc on 2023/6/23.
 */
class ContentViewController<VB : ViewBinding>(
    private val baseView: BaseView,
    private val inflater: LayoutInflater,
    private val container: ViewGroup? = null,
    private val annotations: List<Annotation>
) {

    // 建立根布局，方便加载标题，加载view，错误view
    private var _rootBinding: ViewRootBinding? = null
    val rootBinding
        get() = run {
            if (_rootBinding == null) {
                _rootBinding =
                    if (container == null)
                        ViewRootBinding.inflate(inflater)
                    else
                        ViewRootBinding.inflate(inflater, container, false)
            }
            _rootBinding!!
        }

    private var _collapsingTitleBinding: ViewCollapsingTitleBinding? = null
    private val collapsingTitleBinding
        get() = run {
            if (_collapsingTitleBinding == null) {
                _collapsingTitleBinding =
                    if (container == null)
                        ViewCollapsingTitleBinding.inflate(inflater)
                    else
                        ViewCollapsingTitleBinding.inflate(inflater, container, false)
            }
            _collapsingTitleBinding!!
        }

    // 错误显示的空布局
    private var _failedViewBinding: ViewFailedBinding? = null
    val failedViewBinding
        get() = run {
            if (_failedViewBinding == null) {
                _failedViewBinding =
                    if (container == null)
                        ViewFailedBinding.inflate(inflater)
                    else
                        ViewFailedBinding.inflate(inflater, container, false)
            }
            _failedViewBinding!!
        }

    private var _bodyBinding: VB? = null
    val bodyBinding
        get() = run {
            if (_bodyBinding == null) {
                _bodyBinding = intoBodyBinding()
            }
            _bodyBinding!!
        }

    @Suppress("UNCHECKED_CAST") // 忽略 as VB 转型警告
    private fun intoBodyBinding(): VB {
        val type = baseView.javaClass.genericSuperclass
        val vbClass: Class<VB> = (type as ParameterizedType).actualTypeArguments[0] as Class<VB>
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return method.invoke(this, inflater) as VB
    }

    fun createView() {
        val titleAnnotation = annotations.find { it is Title } as Title?
        if (titleAnnotation == null) {
            // layoutBody 加入到body的第一个view，以免阻挡load view
            rootBinding.layoutBody.addView(
                bodyBinding.root,
                0,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        } else {
            when (titleAnnotation.titleType) {
                TitleType.COLL -> {
                    rootBinding.layoutBody.addView(
                        collapsingTitleBinding.root,
                        0,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    collapsingTitleBinding.layoutBody.addView(bodyBinding.root)
                    collapsingTitleBinding.run {
                        if (titleAnnotation.isBack) {
                            toolbar.setNavigationIcon(R.drawable.icon_toolbar_back)
                            toolbar.setNavigationOnClickListener {
                                baseView.goBack()
                            }
                        } else {
                            toolbarLeftView.layoutParams.width =
                                DensityUtils.dp2px(ContextManager.getContext(), 20.0)
                        }
                        collapsingToolbar.title = titleAnnotation.title// 标题
                    }
                }

                else -> {
                    rootBinding.layoutBody.addView(
                        bodyBinding.root,
                        0,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )

                    rootBinding.titleStub.setOnInflateListener { _, view ->
                        val titleBinding = ViewDefaultTitleBinding.bind(view)
                        titleBinding.run {
                            if (titleAnnotation.isBack) {
                                toolbar.setNavigationIcon(R.drawable.icon_toolbar_back)
                                toolbar.setNavigationOnClickListener {
                                    baseView.goBack()
                                }
                            }
                            toolbarTitleView.text = titleAnnotation.title
                        }
                    }
                    rootBinding.titleStub.inflate()
                }
            }

        }

    }

    /**
     * 显示异常布局
     */
    fun showFailedView(failedViewModel: FailedViewModel) {
        when(failedViewModel){
            is FailedViewModel.HiddenView -> removeFailedView()
            else -> {
                failedViewBinding.icon.setImageResource(failedViewModel.resId)
                failedViewBinding.textFailed.text = failedViewModel.failedText

                val targetView = baseView.onFailedViewTarget()?:rootBinding.layoutBody
                failedViewBinding.root.let { view ->
                    if (!targetView.contains(view)) {
                        if(failedViewModel.isClick){
                            view.setOnClickListener {
                                baseView.onFailedViewReload()
                            }
                        }
                        targetView.addView(view)
                    }
                }
            }
        }
    }

    private fun removeFailedView(){
        val targetView = baseView.onFailedViewTarget()?:rootBinding.layoutBody
        failedViewBinding.root.let { view ->
            if(targetView.contains(view)){
                targetView.removeView(view)
            }
        }
    }

    fun clean() {
        // 官方文档： Fragment 的存在时间比其视图长。请务必在 Fragment 的 onDestroyView() 方法中清除对绑定类实例的所有引用。
        _rootBinding = null
        _collapsingTitleBinding = null
        _bodyBinding = null
        _failedViewBinding = null
    }
}