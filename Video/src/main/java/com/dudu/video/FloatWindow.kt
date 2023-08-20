package com.dudu.video

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.dudu.common.util.ContextManager


/**
 * 功能介绍
 * Created by Dzc on 2023/8/19.
 */
class FloatWindow {

    val windowManager: WindowManager by lazy {
        ContextManager.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    val layoutParams: WindowManager.LayoutParams by lazy {
        val layoutParams = WindowManager.LayoutParams()

        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        layoutParams.width = (point.x * 0.4f).toInt()
        layoutParams.height = (point.x * 0.4f).toInt()

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = point.x
        layoutParams.y = (point.y * 0.3f).toInt()

        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        layoutParams.windowAnimations = 0

        layoutParams.format = PixelFormat.RGBA_8888

        layoutParams
    }

    var mView: View? = null

    fun init(view: View) {
        mView ?: let {
            mView = view
            windowManager.addView(view, layoutParams)
        }
    }

    fun dismiss() {
        mView?.let {
            windowManager.removeView(mView)
            mView = null
        }
    }

    fun isShow(): Boolean {
        mView?.let {
            return true
        }?:let {
            return false
        }
    }

}