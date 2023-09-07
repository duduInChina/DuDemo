package com.dudu.common.util

import android.content.Context

/**
 * 功能介绍
 * Created by Dzc on 2023/5/14.
 */
object DensityUtils {
    @JvmStatic
    fun dp2px(context: Context, dpValue: Double): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 屏幕高度像素
     */
    fun getScreenHeightPixels() = ContextManager.getContext().resources.displayMetrics.heightPixels


}