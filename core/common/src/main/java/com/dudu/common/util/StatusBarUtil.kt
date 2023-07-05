package com.dudu.common.util

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 根据 30 SDK,状态栏API进行配置，参考：https://blog.csdn.net/Wait_For_Loading/article/details/119330990
 * 旧版本调整window flags，相关属性已经废弃，从30sdk开始使用相关api进行配置
 * Created by Dzc on 2023/5/7.
 */
object StatusBarUtil {
    /**
     *  沉浸式标题栏，状态栏透明，布局内容会顶到状态栏，通常用于布局背景图片沉浸式情况
     *  如在根布局设置，android:fitsSystemWindows="true"，布局顶部预留状态栏空间
     *  @param backgroundColor 根据该颜色判断状态栏字体黑/白
     */
    fun immersiveStatusBar(window: Window, @ColorInt backgroundColor: Int){
        // 状态栏背景色透明
        window.statusBarColor = Color.TRANSPARENT
        // 设置字体颜色
        setStatusBarTextColor(window, backgroundColor)
        // 把内容放到系统窗口里面, 布局内容会顶到状态栏
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    /**
     * 设置状态栏背景颜色，通常用于标题栏情况，默认不设置直接走themes中statusBarColor属性设置
     */
    fun setStatusBarColor(window: Window, @ColorInt backgroundColor: Int){
        // 状态栏背景色透明
        window.statusBarColor = backgroundColor
        // 设置字体颜色
        setStatusBarTextColor(window, backgroundColor)
    }

    /**
     * 只能控制字体颜色为 黑/白
     */
    fun setStatusBarTextColor(window: Window, @ColorInt backgroundColor: Int){
        // 计算背景颜色亮度
        val calculateLuminance = ColorUtils.calculateLuminance(backgroundColor)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            if(backgroundColor == Color.TRANSPARENT){
                // 如果是透明设置为黑色
                controller.isAppearanceLightStatusBars = true
            } else {
                // 通过亮度决定字体颜色黑白
                controller.isAppearanceLightStatusBars = calculateLuminance >= 0.5
            }
        }
    }

    private var statusBarHeight = -1

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        if(statusBarHeight == -1){
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if(resourceId > 0){
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return statusBarHeight
    }

}